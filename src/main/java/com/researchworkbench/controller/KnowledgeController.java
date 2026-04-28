package com.researchworkbench.controller;

import com.researchworkbench.dto.ApiResponse;
import com.researchworkbench.dto.knowledge.KnowledgeDtos;
import com.researchworkbench.service.KnowledgeService;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @GetMapping("/folders")
    public ApiResponse<List<KnowledgeDtos.FolderResponse>> getFolders() {
        return ApiResponse.ok(knowledgeService.getFolders());
    }

    @PostMapping("/folders")
    public ApiResponse<KnowledgeDtos.FolderResponse> createFolder(@Valid @RequestBody KnowledgeDtos.FolderCreateRequest request) {
        return ApiResponse.ok(knowledgeService.createFolder(request));
    }

    @PutMapping("/folders/{folderId}")
    public ApiResponse<KnowledgeDtos.FolderResponse> renameFolder(
        @PathVariable Long folderId,
        @Valid @RequestBody KnowledgeDtos.FolderRenameRequest request
    ) {
        return ApiResponse.ok(knowledgeService.renameFolder(folderId, request));
    }

    @DeleteMapping("/folders/{folderId}")
    public ApiResponse<Void> deleteFolder(@PathVariable Long folderId) {
        knowledgeService.deleteFolder(folderId);
        return ApiResponse.ok(null, "Folder deleted");
    }

    @PostMapping(value = "/folders/{folderId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<KnowledgeDtos.FolderResponse> uploadDocuments(
        @PathVariable Long folderId,
        @RequestPart("files") MultipartFile[] files
    ) {
        return ApiResponse.ok(knowledgeService.uploadDocuments(folderId, files));
    }

    @DeleteMapping("/documents/{documentId}")
    public ApiResponse<Void> deleteDocument(@PathVariable Long documentId) {
        knowledgeService.deleteDocument(documentId);
        return ApiResponse.ok(null, "Document deleted");
    }

    @DeleteMapping("/documents")
    public ApiResponse<Void> batchDelete(@RequestParam("ids") String ids) {
        List<Long> documentIds = Arrays.stream(ids.split(","))
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .map(Long::valueOf)
            .toList();
        knowledgeService.batchDelete(documentIds);
        return ApiResponse.ok(null, "Documents deleted");
    }

    @PostMapping("/ask")
    public ApiResponse<KnowledgeDtos.KnowledgeAnswerResponse> ask(@Valid @RequestBody KnowledgeDtos.KnowledgeAskRequest request) {
        return ApiResponse.ok(knowledgeService.ask(request));
    }

    @GetMapping("/search")
    public ApiResponse<KnowledgeDtos.KnowledgeSearchResponse> search(
        @RequestParam("keyword") String keyword,
        @RequestParam(value = "folderId", required = false) Long folderId
    ) {
        return ApiResponse.ok(knowledgeService.search(keyword, folderId));
    }

    @GetMapping("/summary")
    public ApiResponse<KnowledgeDtos.KnowledgeSummaryResponse> summarize(
        @RequestParam(value = "folderId", required = false) Long folderId
    ) {
        return ApiResponse.ok(knowledgeService.summarize(folderId));
    }
}
