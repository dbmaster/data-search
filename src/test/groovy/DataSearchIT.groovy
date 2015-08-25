import io.dbmaster.testng.BaseToolTestNGCase;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test

import com.branegy.tools.api.ExportType;


public class DataSearchIT extends BaseToolTestNGCase {

    @Test
    @Parameters(["p_database","p_search_terms","p_max_rows"])
    public void test(String p_database, String p_search_terms, int p_max_rows) {
        def parameters = [ "p_database"     :  p_database,
                           "p_search_terms" :  p_search_terms,
                           "p_max_rows"     :  p_max_rows  ]

        String found_tables = tools.toolExecutor("data-search", parameters).execute()
        //def bindings  = handler.getBinding()
        //def found_tables = bindings["search_result"]
        System.out.println("done! "+found_tables)

        //assert found_tables == ['Person.Person'] : "Unexpected search results ${found_tables}"
        //println "test";
    }
}
