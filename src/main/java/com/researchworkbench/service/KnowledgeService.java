package com.researchworkbench.service;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.KnowledgeDocument;
import com.researchworkbench.domain.KnowledgeFolder;
import com.researchworkbench.dto.knowledge.KnowledgeDtos;
import com.researchworkbench.exception.NotFoundException;
import com.researchworkbench.repository.KnowledgeDocumentRepository;
import com.researchworkbench.repository.KnowledgeFolderRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class KnowledgeService {

    private static final DateTimeFormatter LABEL_FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    private final CurrentUserService currentUserService;
    private final KnowledgeFolderRepository knowledgeFolderRepository;
    private final KnowledgeDocumentRepository knowledgeDocumentRepository;
    private final FileStorageService fileStorageService;

    public KnowledgeService(
        CurrentUserService currentUserService,
        KnowledgeFolderRepository knowledgeFolderRepository,
        KnowledgeDocumentRepository knowledgeDocumentRepository,
        FileStorageService fileStorageService
    ) {
        this.currentUserService = currentUserService;
        this.knowledgeFolderRepository = knowledgeFolderRepository;
        this.knowledgeDocumentRepository = knowledgeDocumentRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<KnowledgeDtos.FolderResponse> getFolders() {
        AppUser user = currentUserService.getCurrentUser();
        return knowledgeFolderRepository.findByUserOrderByCreatedAtAsc(user).stream()
            .map(this::toFolderResponse)
            .toList();
    }

    @Transactional
    public KnowledgeDtos.FolderResponse createFolder(KnowledgeDtos.FolderCreateRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        KnowledgeFolder folder = new KnowledgeFolder();
        folder.setUser(user);
        folder.setName(request.name().trim());
        return toFolderResponse(knowledgeFolderRepository.save(folder));
    }

    @Transactional
    public KnowledgeDtos.FolderResponse renameFolder(Long folderId, KnowledgeDtos.FolderRenameRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        KnowledgeFolder folder = knowledgeFolderRepository.findByIdAndUser(folderId, user)
            .orElseThrow(() -> new NotFoundException("Knowledge folder not found"));
        folder.setName(request.name().trim());
        return toFolderResponse(knowledgeFolderRepository.save(folder));
    }

    @Transactional
    public void deleteFolder(Long folderId) {
        AppUser user = currentUserService.getCurrentUser();
        KnowledgeFolder folder = knowledgeFolderRepository.findByIdAndUser(folderId, user)
            .orElseThrow(() -> new NotFoundException("Knowledge folder not found"));
        knowledgeFolderRepository.delete(folder);
    }

    @Transactional
    public KnowledgeDtos.FolderResponse uploadDocuments(Long folderId, MultipartFile[] files) {
        AppUser user = currentUserService.getCurrentUser();
        KnowledgeFolder folder = knowledgeFolderRepository.findByIdAndUser(folderId, user)
            .orElseThrow(() -> new NotFoundException("Knowledge folder not found"));

        for (MultipartFile file : files) {
            KnowledgeDocument document = new KnowledgeDocument();
            document.setFolder(folder);
            document.setOriginalName(file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename());
            document.setStoragePath(fileStorageService.store(file, "knowledge"));
            document.setContentType(file.getContentType());
            document.setSizeBytes(file.getSize());
            document.setSizeLabel(formatFileSize(file.getSize()));
            document.setUpdatedAtLabel(LABEL_FORMATTER.format(LocalDateTime.now()));
            document.setSummary("新上传文件，待总结: " + document.getOriginalName());
            knowledgeDocumentRepository.save(document);
            folder.getDocuments().add(document);
        }

        return toFolderResponse(folder);
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        AppUser user = currentUserService.getCurrentUser();
        KnowledgeDocument document = knowledgeDocumentRepository.findById(documentId)
            .filter(item -> item.getFolder().getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new NotFoundException("Knowledge document not found"));
        knowledgeDocumentRepository.delete(document);
    }

    @Transactional
    public void batchDelete(List<Long> ids) {
        AppUser user = currentUserService.getCurrentUser();
        List<KnowledgeDocument> documents = knowledgeDocumentRepository.findByIdIn(ids).stream()
            .filter(item -> item.getFolder().getUser().getId().equals(user.getId()))
            .toList();
        knowledgeDocumentRepository.deleteAll(documents);
    }

    @Transactional(readOnly = true)
    public KnowledgeDtos.KnowledgeAnswerResponse ask(KnowledgeDtos.KnowledgeAskRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        List<KnowledgeFolder> folders = knowledgeFolderRepository.findByUserOrderByCreatedAtAsc(user);
        KnowledgeFolder folder = resolveFolder(request.folderId(), folders);
        String docNames = folder.getDocuments().stream().limit(3).map(KnowledgeDocument::getOriginalName).reduce((a, b) -> a + ", " + b).orElse("暂无文件");
        String answer = "基于知识库“" + folder.getName() + "”的回答: 推荐优先引用 " + docNames
            + "，并把结论拆成“研究问题 / 方法 / 证据来源 / 局限”四段。";
        return new KnowledgeDtos.KnowledgeAnswerResponse(request.question(), answer);
    }

    @Transactional(readOnly = true)
    public KnowledgeDtos.KnowledgeSearchResponse search(String keyword, Long folderId) {
        AppUser user = currentUserService.getCurrentUser();
        List<KnowledgeDocument> matches = new ArrayList<>();
        if (folderId != null) {
            matches.addAll(knowledgeDocumentRepository.findByFolderIdAndOriginalNameContainingIgnoreCaseOrFolderIdAndSummaryContainingIgnoreCase(
                folderId, keyword, folderId, keyword
            ));
        } else {
            for (KnowledgeFolder folder : knowledgeFolderRepository.findByUserOrderByCreatedAtAsc(user)) {
                matches.addAll(folder.getDocuments().stream()
                    .filter(document -> document.getOriginalName().toLowerCase().contains(keyword.toLowerCase())
                        || document.getSummary().toLowerCase().contains(keyword.toLowerCase()))
                    .toList());
            }
        }

        return new KnowledgeDtos.KnowledgeSearchResponse(keyword, matches.stream().map(this::toDocumentResponse).toList());
    }

    @Transactional(readOnly = true)
    public KnowledgeDtos.KnowledgeSummaryResponse summarize(Long folderId) {
        AppUser user = currentUserService.getCurrentUser();
        List<KnowledgeFolder> folders = knowledgeFolderRepository.findByUserOrderByCreatedAtAsc(user);
        KnowledgeFolder folder = resolveFolder(folderId, folders);
        String summary = "知识库“" + folder.getName() + "”共 " + folder.getDocuments().size()
            + " 份文件，当前更适合做“检索增强 + 证据回查”的问答与综述整理流程。";
        return new KnowledgeDtos.KnowledgeSummaryResponse(folder.getId(), summary);
    }

    private KnowledgeFolder resolveFolder(Long folderId, List<KnowledgeFolder> folders) {
        if (folderId != null) {
            return folders.stream()
                .filter(folder -> folder.getId().equals(folderId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Knowledge folder not found"));
        }

        return folders.stream().findFirst().orElseThrow(() -> new NotFoundException("No knowledge folder available"));
    }

    private KnowledgeDtos.FolderResponse toFolderResponse(KnowledgeFolder folder) {
        return new KnowledgeDtos.FolderResponse(
            folder.getId(),
            folder.getName(),
            folder.getDocuments().stream().map(this::toDocumentResponse).toList()
        );
    }

    private KnowledgeDtos.DocumentResponse toDocumentResponse(KnowledgeDocument document) {
        return new KnowledgeDtos.DocumentResponse(
            document.getId(),
            document.getOriginalName(),
            document.getSizeLabel(),
            document.getUpdatedAtLabel(),
            document.getSummary()
        );
    }

    private String formatFileSize(long bytes) {
        if (bytes >= 1024 * 1024) {
            return String.format("%.1f MB", bytes / 1024.0 / 1024.0);
        }
        if (bytes >= 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        return bytes + " B";
    }
}
