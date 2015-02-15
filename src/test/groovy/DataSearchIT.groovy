import io.dbmaster.testng.BaseToolTestNGCase;

import org.testng.annotations.Test

import com.branegy.tools.api.ExportType;


public class DataSearchIT extends BaseToolTestNGCase {

    @Test
    public void test() {
        def parameters = [ "p_database"     :  getTestProperty("p_database"),
                           "p_search_terms" :  getTestProperty("p_search_terms"),
                           "p_max_rows"     :  Integer.valueOf(getTestProperty("p_max_rows"))  ]

        String found_tables = tools.toolExecutor("data-search", parameters).execute()
        //def bindings  = handler.getBinding()
        //def found_tables = bindings["search_result"]
        System.out.println("done! "+found_tables)

        //assert found_tables == ['Person.Person'] : "Unexpected search results ${found_tables}"
        //println "test";
    }
}
