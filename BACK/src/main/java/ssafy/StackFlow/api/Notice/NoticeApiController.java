package ssafy.StackFlow.api.Notice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ssafy.StackFlow.Domain.notice.Notice;
import ssafy.StackFlow.Domain.user.Signup;
import ssafy.StackFlow.Service.notice.NoticeService;
import ssafy.StackFlow.Service.user.UserService;
import ssafy.StackFlow.api.Notice.dto.FileDto;
import ssafy.StackFlow.api.Notice.dto.NoticeDto;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/notice")
@RequiredArgsConstructor
@RestController  // 프론트 api 넘겨주기 (json 응답 반환)
public class NoticeApiController {
    // ----------------------------------
    // **API 응답을 위한 엔드포인트 추가**
    // ----------------------------------

    private final NoticeService noticeService;
    private final UserService userService;

    // 공지사항 목록 조회 (API)
    @GetMapping("/api")
    public ResponseEntity<List<NoticeDto>> getNoticeList() {
        List<Notice> notices = this.noticeService.getList();

        // Notice 객체를 NoticeDto로 변환
        List<NoticeDto> noticeDtos = notices.stream()
                .map(notice -> {
                    List<FileDto> fileDtos = notice.getFiles().stream()
                            .map(file -> new FileDto(file.getId(), file.getFileName(), file.getFilePath(), file.getFileType()))
                            .collect(Collectors.toList());

                    // NoticeDto 생성
                    return new NoticeDto(notice.getId(), notice.getTitle(), notice.getContent(), notice.getCreatedAt(), notice.getUpdatedAt(), fileDtos);
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(noticeDtos, HttpStatus.OK);
    }

    // 공지사항 상세 조회 (API)
    @GetMapping("/api/{id}")
    public ResponseEntity<NoticeDto> getNotice(@PathVariable("id") Long id) {
        Notice notice = this.noticeService.getNotice(id);

        // 파일 정보를 FileDto로 변환
        List<FileDto> fileDtos = notice.getFiles().stream()
                .map(file -> new FileDto(file.getId(), file.getFileName(), file.getFilePath(), file.getFileType()))
                .collect(Collectors.toList());

        // NoticeDto 생성
        NoticeDto noticeDto = new NoticeDto(notice.getId(), notice.getTitle(), notice.getContent(), notice.getCreatedAt(), notice.getUpdatedAt(), fileDtos);

        return new ResponseEntity<>(noticeDto, HttpStatus.OK);
    }

    // 공지사항 생성 (API)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/api/create")
    public ResponseEntity<Notice> createNotice(@RequestParam("title") String title,
                                               @RequestParam("content") String content,
                                               Principal principal) {
        // 로그인한 사용자의 정보를 바탕으로 공지 생성
        Signup signup = this.userService.getUser(principal.getName());
        Notice notice = this.noticeService.create(title, content, signup);
        return new ResponseEntity<>(notice, HttpStatus.CREATED);
    }

    // 공지사항 수정 (API)
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/api/modify/{id}")
    public ResponseEntity<Notice> modifyNotice(@PathVariable("id") Long id,
                                               @RequestParam("title") String title,
                                               @RequestParam("content") String content,
                                               Principal principal) {
        Notice notice = this.noticeService.getNotice(id);
        if (!notice.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }
        this.noticeService.modify(notice, title, content);
        return new ResponseEntity<>(notice, HttpStatus.OK);
    }

    // 공지사항 삭제 (API)
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/api/delete/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("id") Long id,
                                             Principal principal) {
        Notice notice = this.noticeService.getNotice(id);
        if (notice == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "공지사항을 찾을 수 없습니다.");
        }
        if (!notice.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }
        this.noticeService.delete(notice);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 삭제 성공
    }
}