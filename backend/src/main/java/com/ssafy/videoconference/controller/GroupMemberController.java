package com.ssafy.videoconference.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.videoconference.controller.command.AddMemberCommand;
import com.ssafy.videoconference.controller.command.ChangeNicknameCommand;
import com.ssafy.videoconference.controller.payload.AddMemberPayload;
import com.ssafy.videoconference.controller.payload.ChangeNicknamePayload;
import com.ssafy.videoconference.controller.result.ApiResult;
import com.ssafy.videoconference.controller.result.GroupMemberResult;
import com.ssafy.videoconference.controller.result.GroupResult;
import com.ssafy.videoconference.controller.result.Result;
import com.ssafy.videoconference.model.group.bean.Group;
import com.ssafy.videoconference.model.group.service.GroupService;
import com.ssafy.videoconference.model.groupmember.bean.GroupMember;
import com.ssafy.videoconference.model.groupmember.service.Group_memberService;
import com.ssafy.videoconference.model.user.bean.CurrentUser;
import com.ssafy.videoconference.model.user.bean.UserDetail;


@CrossOrigin(origins = { "*" }, maxAge = 6000)
@RestController
@RequestMapping("/api/groupmember")
public class GroupMemberController {
	
	
	@Autowired
	private Group_memberService gmService;
	
	@Autowired
	private GroupService groupService;
	
	
	@PostMapping("/add")
	public ResponseEntity<ApiResult> addMember(@RequestBody AddMemberPayload payload,
												@CurrentUser UserDetail user) {
		AddMemberCommand command = payload.toCommand();
		GroupMember gm = gmService.addMember(command);
		List<Group> gp_list_host = groupService.findByHostId(user.getId());
		List<Group> gp_list_member = groupService.findByUserId(user.getId());
		return GroupResult.build(gp_list_host, gp_list_member);
	}
	
	
	@GetMapping("/get/{groupNo}/{id}")
	public ResponseEntity<ApiResult> getMember(@PathVariable("groupNo") int groupNo,
												@PathVariable("id") String id) {
		GroupMember gm = gmService.findMember(groupNo, id);
		return GroupMemberResult.build(gm);
	}
	
	
	@GetMapping("/getid/{groupMemberNo}")
	public ResponseEntity<ApiResult> getMemberById(@PathVariable("groupMemberNo") int groupMemberNo) {
		GroupMember gm = gmService.findById(groupMemberNo);
		return GroupMemberResult.build(gm);
	}
	
	
	@GetMapping("/getno/{groupNo}")
	public ResponseEntity<ApiResult> getMemberByNo(@PathVariable("groupNo") int groupNo) {
		List<GroupMember> gm_list = gmService.findByNo(groupNo);
		return GroupMemberResult.build(gm_list);
	}
	
	
	@DeleteMapping("/delno/{groupNo}/{id}")
	public ResponseEntity<ApiResult> deleteById(@PathVariable("groupNo") int groupNo,
												@PathVariable("id") String id) {
		gmService.deleteById(groupNo, id);
		List<GroupMember> gm_list = gmService.findByNo(groupNo);
		return GroupMemberResult.build(gm_list);
	}
	
	
	@PutMapping("/name/{groupMemberNo}")
	public ResponseEntity<ApiResult> changeNickname(@PathVariable("groupMemberNo") int groupMemberNo,
														@RequestBody ChangeNicknamePayload payload) {
		ChangeNicknameCommand command = payload.toCommand(groupMemberNo);
		gmService.changeNickname(command);
		return Result.ok();
	}
	
	
}