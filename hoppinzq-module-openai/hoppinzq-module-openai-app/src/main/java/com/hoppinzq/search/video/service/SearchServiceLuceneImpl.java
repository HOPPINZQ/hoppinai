package com.hoppinzq.search.video.service;

import com.hoppinzq.search.SearchService;
import com.hoppinzq.search.video.dao.MyVideoMapper;
import com.hoppinzq.search.video.po.VideoPO;
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
public class SearchServiceLuceneImpl implements SearchService {

    private static final String indexPath = "D:\\index\\video";
    @Autowired
    private MyVideoMapper myVideoMapper;

    @Override
    @ApiMapping(value = "initVideo", title = "初始化", description = "初始化视频")
    public void init() {
        List<VideoPO> videoPOS = myVideoMapper.selectList();
        try {
            List<Document> docList = new ArrayList<>();
            for (VideoPO videoPO : videoPOS) {
                Document document = new Document();
                document.add(new StringField("id", videoPO.getVideoId(), Field.Store.YES));
                document.add(new TextField("name", videoPO.getVideoName(), Field.Store.YES));
                document.add(new TextField("description", videoPO.getVideoMiaoshu() == null ? videoPO.getVideoMiaoshu() : videoPO.getVideoMiaoshu(), Field.Store.YES));
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
            throw new RuntimeException("初始化视频失败:" + ex);
        }
    }

    /**
     * 搜索博客
     *
     * @param searchContent
     * @return
     */
    @Override
    @ApiMapping(value = "queryVideo", title = "搜索视频", description = "搜索视频")
    public List<VideoPO> query(String searchContent) {
        Analyzer analyzer = new IKAnalyzer();
        Integer start = 0;
        Integer end = 10;
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        String[] fields = {"name", "description"};
        Map<String, Float> boots = new HashMap<>();
        boots.put("name", 3f);
        boots.put("description", 2f);
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
                    return myVideoMapper.selectBatchIds(ids);
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
