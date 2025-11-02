package com.hoppinzq.search.blog.service;

import com.hoppinzq.query.LambdaQueryWrapperX;
import com.hoppinzq.search.SearchService;
import com.hoppinzq.search.blog.dao.MyBlogMapper;
import com.hoppinzq.search.blog.po.BlogPO;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.nio.file.Paths;
import java.util.*;

@ApiServiceMapping(title = "搜索引擎", description = "搜索引擎服务", roleType = ApiServiceMapping.RoleType.NO_RIGHT)
public class SearchBlogServiceLuceneImpl implements SearchService {

    private static final String indexPath = "D:\\index\\blog";
    @Autowired
    private MyBlogMapper myBlogMapper;

    @Override
    @ApiMapping(value = "initBlog", title = "初始化", description = "初始化博客")
    public void init() {
        List<BlogPO> blogPOS = myBlogMapper.selectList(new LambdaQueryWrapperX<BlogPO>().eq(BlogPO::getType, 0));
        try {
            List<Document> docList = new ArrayList<>();
            for (BlogPO blogPO : blogPOS) {
                Document document = new Document();
                document.add(new StringField("id", blogPO.getId(), Field.Store.YES));
                document.add(new TextField("title", blogPO.getTitle(), Field.Store.YES));
                if (blogPO.getDescription() != null) {
                    document.add(new TextField("description", blogPO.getDescription(), Field.Store.YES));
                }
                document.add(new TextField("text", blogPO.getText(), Field.Store.YES));
                document.add(new StringField("authorName", blogPO.getAuthorName(), Field.Store.YES));
                docList.add(document);
            }
            Analyzer analyzer = new IKAnalyzer();
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(dir, config);
            for (Document doc : docList) {
                indexWriter.addDocument(doc);
            }
            indexWriter.close();
        } catch (Exception ex) {
            throw new RuntimeException("初始化博客失败:" + ex);
        }
    }

    /**
     * 搜索博客
     *
     * @param searchContent
     * @return
     */
    @Override
    @ApiMapping(value = "queryBlog2", title = "搜索博客", description = "搜索博客")
    public List<BlogPO> query(String searchContent) {
        Analyzer analyzer = new IKAnalyzer();
        Integer start = 0;
        Integer end = 10;
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        String[] fields = {"title", "authorName", "description", "className", "text"};
        Map<String, Float> boots = new HashMap<>();
        boots.put("title", 5f);
        boots.put("authorName", 4f);
        boots.put("description", 3f);
        boots.put("text", 2f);
        try {
            MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields, analyzer, boots);
            Query querySearch = multiFieldQueryParser.parse(searchContent);
            query.add(querySearch, BooleanClause.Occur.MUST);
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            IndexReader indexReader = DirectoryReader.open(dir);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            TopDocs topDocs = indexSearcher.search(query.build(), end);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            List<String> ids = new ArrayList<>();
            if (scoreDocs != null) {
                for (int i = start; i < end; i++) {
                    if (start > topDocs.totalHits || topDocs.totalHits == i) {
                        break;
                    }
                    int docID = scoreDocs[i].doc;
                    Document doc = indexReader.document(docID);
                    String id = doc.get("id");
                    ids.add(id);
                }
                if (ids.size() != 0) {
                    return myBlogMapper.selectBatchIds(ids);
                } else {
                    return Collections.emptyList();
                }
            }
            indexReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return Collections.emptyList();
    }

}
