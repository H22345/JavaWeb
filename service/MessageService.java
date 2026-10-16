package SHmarketplace.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import SHmarketplace.dto.MessageSendDTO;
import SHmarketplace.entity.Message;

import java.util.List;

/**
 * 留言业务接口
 */
public interface MessageService {

    /**
     * 发送留言（买家对商品卖家留言）
     *
     * @param fromUserId 留言人ID（从登录态获取）
     */
    Message sendMessage(@NotNull Integer fromUserId, @Valid MessageSendDTO dto);

    /**
     * 卖家回复留言
     *
     * @param id             留言 ID
     * @param reply          回复内容
     * @param currentUserId  当前登录用户 ID（用于权限校验：只有接收人/卖家能回复）
     */
    void replyMessage(@NotNull Integer id, @NotBlank String reply, @NotNull Long currentUserId);

    /**
     * 查询某商品的所有留言
     */
    List<Message> listByGoods(@NotNull Integer goodsId);

    /**
     * 查询卖家收到的留言
     */
    List<Message> listReceived(@NotNull Integer sellerId);

    /**
     * 查询买家发出的留言
     */
    List<Message> listSent(@NotNull Integer fromUserId);
}