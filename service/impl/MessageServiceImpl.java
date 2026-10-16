package SHmarketplace.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import SHmarketplace.dto.MessageSendDTO;
import SHmarketplace.entity.Goods;
import SHmarketplace.entity.Message;
import SHmarketplace.entity.User;
import SHmarketplace.exception.BusinessException;
import SHmarketplace.exception.ErrorCode;
import SHmarketplace.mapper.MessageMapper;
import SHmarketplace.service.GoodsService;
import SHmarketplace.service.MessageService;
import SHmarketplace.service.UserService;

import java.util.List;

/**
 * 留言业务实现
 */
@Service
@Validated
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final GoodsService goodsService;
    private final UserService userService;

    @Override
    public Message sendMessage(Integer fromUserId, MessageSendDTO dto) {
        // 1. 校验留言人存在
        User buyer = userService.findById((long) fromUserId);
        if (buyer == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "留言人不存在");
        }

        // 2. 校验商品存在（商品下架了也能留言，只是可能卖家看不到回复）
        Goods goods = goodsService.findById((long) dto.getGoodsId());
        if (goods == null) {
            throw new BusinessException(ErrorCode.GOODS_NOT_FOUND);
        }

        // 3. 校验接收人存在
        User seller = userService.findById((long) dto.getToUserId());
        if (seller == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "接收人不存在");
        }

        // 4. 留言人不能给自己留言
        if (fromUserId.equals(dto.getToUserId())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "不能给自己留言");
        }

        // 5. 组装 + 插入
        Message message = Message.builder()
                .goodsId(dto.getGoodsId())
                .fromUserId(fromUserId)
                .toUserId(dto.getToUserId())
                .content(dto.getContent())
                .reply(null)
                .build();

        int rows = messageMapper.insert(message);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "留言发送失败");
        }
        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyMessage(Integer id, String reply, Long currentUserId) {
        // 1. 校验留言存在
        Message message = messageMapper.selectById(id);
        if (message == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "留言不存在");
        }

        // 2. 权限校验：只有接收人（toUserId，即卖家）才能回复
        if (!message.getToUserId().equals(currentUserId.intValue())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "只有卖家才能回复该留言");
        }

        // 3. 留言不能重复回复（根据业务决定，允许覆盖回复的话去掉这个判断）
        if (message.getReply() != null && !message.getReply().isBlank()) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "留言已回复，不可重复回复");
        }

        // 4. 更新回复
        int rows = messageMapper.replyMessage(id, reply);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "回复失败");
        }
    }

    @Override
    public List<Message> listByGoods(Integer goodsId) {
        return messageMapper.selectByGoodsId(goodsId);
    }

    @Override
    public List<Message> listReceived(Integer sellerId) {
        return messageMapper.selectBySellerId(sellerId);
    }

    @Override
    public List<Message> listSent(Integer fromUserId) {
        return messageMapper.selectByFromUserId(fromUserId);
    }
}