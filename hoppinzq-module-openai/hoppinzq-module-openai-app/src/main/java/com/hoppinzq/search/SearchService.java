package com.hoppinzq.search;

import java.util.List;

public interface SearchService {

    void init();

    List<? extends SearchPO> query(String searchContent);
}
