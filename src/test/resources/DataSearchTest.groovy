package org.dbmaster.tools.db_free_text_search;

import com.branegy.testing.dbmaster.tools.BaseToolTestNGCase;


class DataSearchTest extends BaseToolTestNGCase {

    @Test
    public void test() {
        def parameters = [ "p_server"       :  "ms_sql_server",
                           "p_database"     :  "AdventureWorks2008",
                           "p_search_terms" :  "Maxwell"]

        def handler = tools.execute("data-search", parameters, false);
        def bindings  = handler.getBinding()
        def found_tables = bindings["search_result"]
        System.out.println(found_tables);

        assert found_tables == ['Person.Person'] : "Unexpected search results ${found_tables}"
    }
}
