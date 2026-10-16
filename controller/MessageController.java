package SHmarketplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import SHmarketplace.annotation.CurrentUser;
import SHmarketplace.common.Result;
import SHmarketplace.dto.MessageSendDTO;
import SHmarketplace.entity.Message;
import SHmarketplace.exception.BusinessException;
import SHmarketplace.exception.ErrorCode;
import SHmarketplace.service.MessageService;

import java.util.List;

/**
 * 留言控制器
 */
@Tag(name = "留言管理", description = "买家留言、卖家回复、留言查询")
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "发送留言（买家留言给卖家）")
    @PostMapping
    public Result<Message> sendMessage(@Valid @RequestBody MessageSendDTO dto,
                                       @CurrentUser Long userId) {
        Message msg = messageService.sendMessage(userId.intValue(), dto);
        return Result.success(msg);
    }

    @Operation(summary = "卖家回复留言")
    @PostMapping("/{id}/reply")
    public Result<Void> reply(@PathVariable Integer id,
                              @RequestParam String reply,
                              @CurrentUser Long userId) {
        messageService.replyMessage(id, reply, userId);
        return Result.success();
    }

    @Operation(summary = "查询某商品的所有留言")
    @GetMapping("/goods/{goodsId}")
    public Result<List<Message>> listByGoods(@PathVariable Integer goodsId) {
        return Result.success(messageService.listByGoods(goodsId));
    }

    @Operation(summary = "查询卖家收到的留言")
    @GetMapping("/received")
    public Result<List<Message>> listReceived(@CurrentUser Long userId) {
        return Result.success(messageService.listReceived(userId.intValue()));
    }

    @Operation(summary = "查询买家发出的留言")
    @GetMapping("/sent")
    public Result<List<Message>> listSent(@CurrentUser Long userId) {
        return Result.success(messageService.listSent(userId.intValue()));
    }
}