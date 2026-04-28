package com.researchworkbench.config;

import com.researchworkbench.domain.AiToolLink;
import com.researchworkbench.domain.AppUser;
import com.researchworkbench.domain.CalendarEvent;
import com.researchworkbench.domain.KnowledgeDocument;
import com.researchworkbench.domain.KnowledgeFolder;
import com.researchworkbench.domain.PunchRecord;
import com.researchworkbench.domain.ResearchMindMapNode;
import com.researchworkbench.domain.ResearchPaper;
import com.researchworkbench.domain.ResearchQuickSummary;
import com.researchworkbench.domain.ResearchSearchHistory;
import com.researchworkbench.domain.ResearchTask;
import com.researchworkbench.domain.SearchSource;
import com.researchworkbench.domain.UserRole;
import com.researchworkbench.domain.WebSearchHistory;
import com.researchworkbench.repository.AiToolLinkRepository;
import com.researchworkbench.repository.AppUserRepository;
import com.researchworkbench.repository.CalendarEventRepository;
import com.researchworkbench.repository.KnowledgeDocumentRepository;
import com.researchworkbench.repository.KnowledgeFolderRepository;
import com.researchworkbench.repository.PunchRecordRepository;
import com.researchworkbench.repository.ResearchMindMapNodeRepository;
import com.researchworkbench.repository.ResearchPaperRepository;
import com.researchworkbench.repository.ResearchQuickSummaryRepository;
import com.researchworkbench.repository.ResearchSearchHistoryRepository;
import com.researchworkbench.repository.ResearchTaskRepository;
import com.researchworkbench.repository.WebSearchHistoryRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapDataConfig {

    @Bean
    public CommandLineRunner seedData(
        AppUserRepository appUserRepository,
        ResearchPaperRepository researchPaperRepository,
        ResearchSearchHistoryRepository researchSearchHistoryRepository,
        ResearchMindMapNodeRepository researchMindMapNodeRepository,
        ResearchQuickSummaryRepository researchQuickSummaryRepository,
        KnowledgeFolderRepository knowledgeFolderRepository,
        KnowledgeDocumentRepository knowledgeDocumentRepository,
        WebSearchHistoryRepository webSearchHistoryRepository,
        AiToolLinkRepository aiToolLinkRepository,
        ResearchTaskRepository researchTaskRepository,
        CalendarEventRepository calendarEventRepository,
        PunchRecordRepository punchRecordRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (appUserRepository.findByEmail("researcher@lab.ai").isPresent()) {
                return;
            }

            AppUser user = new AppUser();
            user.setEmail("researcher@lab.ai");
            user.setPasswordHash(passwordEncoder.encode("12345678"));
            user.setDisplayName("林知遥");
            user.setTitle("医学信息学研究者");
            user.setInstitution("临床数据与智能研究中心");
            user.setBio("关注生物医学知识库、综述自动化与研究工作流设计。");
            user.setAvatarUrl("https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=320&q=80");
            user.setRole(UserRole.USER);
            user.setWechatLinked(true);
            user.setWechatAccount("lin-research");
            user.setFeishuLinked(false);
            user.setFeishuAccount("未关联");
            appUserRepository.save(user);

            KnowledgeFolder folder1 = new KnowledgeFolder();
            folder1.setUser(user);
            folder1.setName("综述草稿");
            knowledgeFolderRepository.save(folder1);

            KnowledgeFolder folder2 = new KnowledgeFolder();
            folder2.setUser(user);
            folder2.setName("实验笔记");
            knowledgeFolderRepository.save(folder2);

            KnowledgeFolder folder3 = new KnowledgeFolder();
            folder3.setUser(user);
            folder3.setName("待清洗资料");
            knowledgeFolderRepository.save(folder3);

            saveDocument(knowledgeDocumentRepository, folder1, "RAG-biomed-review.pdf", "2.4 MB", "今天 08:42", "检索增强 + 证据对齐");
            saveDocument(knowledgeDocumentRepository, folder1, "multi-agent-outline.md", "128 KB", "昨天 21:18", "章节结构与写作提示");
            saveDocument(knowledgeDocumentRepository, folder2, "single-cell-notes.docx", "684 KB", "04-25", "实验流程与参数记录");
            saveDocument(knowledgeDocumentRepository, folder3, "web-clips-batch.zip", "8.7 MB", "04-23", "待提炼来源与标签");

            savePaper(researchPaperRepository, user,
                "Retrieval-Augmented Literature Agents for Biomedical Review Writing",
                "L. Chen, M. Torres, Y. Huang", "2026", "Nature Machine Intelligence",
                "将检索增强代理用于综述写作，强调来源追踪、引文对齐与结构化摘要融合。", "RAG,Biomedical,Survey Agent", 84, true);
            savePaper(researchPaperRepository, user,
                "Mind-Mapped Evidence Synthesis from Multi-PDF Scientific Corpora",
                "E. Rossi, J. Kim", "2025", "ACL Findings",
                "利用层级图谱组织论文证据链，将主题、实验和结论映射到思维导图节点。", "Evidence Graph,Mind Map", 46, false);
            savePaper(researchPaperRepository, user,
                "Fast Abstractive Summaries for Review Planning with Citation Guarantees",
                "K. Singh, A. Doyle", "2025", "EMNLP",
                "聚焦带引文约束的快速总结，适用于系统综述与文献调研首页摘要。", "Summarization,Citation", 67, false);

            saveHistory(researchSearchHistoryRepository, user, "单细胞转录组知识库工作流", 32, "09:30");
            saveHistory(researchSearchHistoryRepository, user, "癌症免疫综述自动化总结", 19, "昨天");
            saveHistory(researchSearchHistoryRepository, user, "文献搜索到思维导图", 11, "周六");

            saveMindMapNode(researchMindMapNodeRepository, user, "论文搜索", "先看综述、再抓方法、最后回查数据集。", true);
            saveMindMapNode(researchMindMapNodeRepository, user, "证据聚类", "按主题、实验对象与评价指标拆分。", false);
            saveMindMapNode(researchMindMapNodeRepository, user, "快速总结", "保留结论、边界条件与可复现实验要点。", false);
            saveMindMapNode(researchMindMapNodeRepository, user, "入库策略", "结果页一键加入指定知识库文件夹。", false);

            saveQuickSummary(researchQuickSummaryRepository, user, "研究问题", "现有文献正从“单轮搜索”转向“检索、聚类、总结、入库”一体化研究台。", "default");
            saveQuickSummary(researchQuickSummaryRepository, user, "方法趋势", "高质量方案都会把思维导图和流程图作为中间层，用于组织证据与章节结构。", "amber");
            saveQuickSummary(researchQuickSummaryRepository, user, "限制条件", "如果知识库中的来源没有被结构化，后续问答和总结的质量会明显下降。", "rose");

            saveWebSearch(webSearchHistoryRepository, user, SearchSource.WEB, "Mermaid 流程图 文献总结",
                "站点内容偏技术教程，适合作为流程图语法参考。", "# Mermaid 流程图 文献总结\n\n- 保留语法模板\n- 抽取流程节点", true);
            saveWebSearch(webSearchHistoryRepository, user, SearchSource.XIAOHONGSHU, "文献调研工作流 模板",
                "高互动内容强调模板化输出与分享复用。", "# 文献调研工作流 模板\n\n- 偏模板化分享\n- 适合抽取清单结构", false);
            saveWebSearch(webSearchHistoryRepository, user, SearchSource.DOUYIN, "论文搜索 快速总结",
                "短视频内容适合抓选题热点和工具对比。", "# 论文搜索 快速总结\n\n- 适合抓热点\n- 适合观察表达方式", false);

            saveTool(aiToolLinkRepository, "OpenAlex Explorer", "适合文献元数据、作者图谱与引文网络初筛。", "https://openalex.org", "外部网页", 1);
            saveTool(aiToolLinkRepository, "Semantic Scholar", "适合高质量论文相关推荐与引用脉络追踪。", "https://www.semanticscholar.org", "外部网页", 2);
            saveTool(aiToolLinkRepository, "PubMed", "Bio 生物医学方向核心入口，适合规范检索。", "https://pubmed.ncbi.nlm.nih.gov", "外部网页", 3);

            saveTask(researchTaskRepository, user, "完成综述引言段落", "本人", LocalDate.now().plusDays(1), "写作", 72);
            saveTask(researchTaskRepository, user, "补充 12 篇对比论文", "本人", LocalDate.now().plusDays(2), "检索", 48);
            saveTask(researchTaskRepository, user, "整理知识库标签结构", "协作者", LocalDate.now().plusDays(3), "整理", 36);

            saveCalendarEvent(calendarEventRepository, user, LocalDate.now().withDayOfMonth(Math.min(3, LocalDate.now().lengthOfMonth())), "论文搜索", "调研");
            saveCalendarEvent(calendarEventRepository, user, LocalDate.now().withDayOfMonth(Math.min(8, LocalDate.now().lengthOfMonth())), "知识库总结", "整理");
            saveCalendarEvent(calendarEventRepository, user, LocalDate.now().withDayOfMonth(Math.min(14, LocalDate.now().lengthOfMonth())), "番茄钟冲刺", "专注");
            saveCalendarEvent(calendarEventRepository, user, LocalDate.now().withDayOfMonth(Math.min(18, LocalDate.now().lengthOfMonth())), "文献流程图输出", "可视化");
            saveCalendarEvent(calendarEventRepository, user, LocalDate.now().withDayOfMonth(Math.min(26, LocalDate.now().lengthOfMonth())), "周回顾", "复盘");

            LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
            List<Boolean> states = List.of(true, true, true, false, true, false, false);
            for (int i = 0; i < states.size(); i++) {
                PunchRecord punchRecord = new PunchRecord();
                punchRecord.setUser(user);
                punchRecord.setPunchDate(weekStart.plusDays(i));
                punchRecord.setCompleted(states.get(i));
                punchRecordRepository.save(punchRecord);
            }
        };
    }

    private void savePaper(
        ResearchPaperRepository repository,
        AppUser user,
        String title,
        String authors,
        String year,
        String journal,
        String summary,
        String tags,
        int citedBy,
        boolean inKnowledgeBase
    ) {
        ResearchPaper paper = new ResearchPaper();
        paper.setUser(user);
        paper.setTitle(title);
        paper.setAuthors(authors);
        paper.setPublicationYear(year);
        paper.setJournal(journal);
        paper.setSummary(summary);
        paper.setTags(tags);
        paper.setCitedBy(citedBy);
        paper.setInKnowledgeBase(inKnowledgeBase);
        repository.save(paper);
    }

    private void saveHistory(ResearchSearchHistoryRepository repository, AppUser user, String keyword, int hits, String timeLabel) {
        ResearchSearchHistory history = new ResearchSearchHistory();
        history.setUser(user);
        history.setKeyword(keyword);
        history.setHits(hits);
        history.setTimeLabel(timeLabel);
        repository.save(history);
    }

    private void saveMindMapNode(ResearchMindMapNodeRepository repository, AppUser user, String title, String detail, boolean accent) {
        ResearchMindMapNode node = new ResearchMindMapNode();
        node.setUser(user);
        node.setTitle(title);
        node.setDetail(detail);
        node.setAccent(accent);
        repository.save(node);
    }

    private void saveQuickSummary(ResearchQuickSummaryRepository repository, AppUser user, String label, String content, String tone) {
        ResearchQuickSummary summary = new ResearchQuickSummary();
        summary.setUser(user);
        summary.setLabel(label);
        summary.setContent(content);
        summary.setTone(tone);
        repository.save(summary);
    }

    private void saveDocument(
        KnowledgeDocumentRepository repository,
        KnowledgeFolder folder,
        String originalName,
        String sizeLabel,
        String updatedAtLabel,
        String summary
    ) {
        KnowledgeDocument document = new KnowledgeDocument();
        document.setFolder(folder);
        document.setOriginalName(originalName);
        document.setStoragePath("seed:" + originalName);
        document.setContentType("application/octet-stream");
        document.setSizeBytes(0L);
        document.setSizeLabel(sizeLabel);
        document.setUpdatedAtLabel(updatedAtLabel);
        document.setSummary(summary);
        repository.save(document);
    }

    private void saveWebSearch(
        WebSearchHistoryRepository repository,
        AppUser user,
        SearchSource source,
        String query,
        String excerpt,
        String markdown,
        boolean saved
    ) {
        WebSearchHistory history = new WebSearchHistory();
        history.setUser(user);
        history.setSource(source);
        history.setQuery(query);
        history.setExcerpt(excerpt);
        history.setMarkdownContent(markdown);
        history.setSaved(saved);
        repository.save(history);
    }

    private void saveTool(AiToolLinkRepository repository, String name, String description, String url, String tag, int sortOrder) {
        AiToolLink toolLink = new AiToolLink();
        toolLink.setName(name);
        toolLink.setDescription(description);
        toolLink.setUrl(url);
        toolLink.setTag(tag);
        toolLink.setSortOrder(sortOrder);
        repository.save(toolLink);
    }

    private void saveTask(
        ResearchTaskRepository repository,
        AppUser user,
        String title,
        String owner,
        LocalDate dueDate,
        String lane,
        int progress
    ) {
        ResearchTask task = new ResearchTask();
        task.setUser(user);
        task.setTitle(title);
        task.setOwnerName(owner);
        task.setDueDate(dueDate);
        task.setLane(lane);
        task.setProgress(progress);
        repository.save(task);
    }

    private void saveCalendarEvent(CalendarEventRepository repository, AppUser user, LocalDate eventDate, String title, String tag) {
        CalendarEvent event = new CalendarEvent();
        event.setUser(user);
        event.setEventDate(eventDate);
        event.setTitle(title);
        event.setTag(tag);
        repository.save(event);
    }
}
