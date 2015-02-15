import io.dbmaster.testng.BaseToolTestNGCase;

import org.testng.annotations.Test

import com.branegy.tools.api.ExportType;


public class DataSearchIT extends BaseToolTestNGCase {

    @Test
    public void test() {
        def parameters = [ "p_database"     :  "10a.SFANOW_DB",
                           "p_search_terms" :  "1",
                           "p_max_rows"     :   1  ]

        String found_tables = tools.toolExecutor("data-search", parameters).execute();
        //def bindings  = handler.getBinding()
        //def found_tables = bindings["search_result"]
        System.out.println("done! "+found_tables.substring(0,3));

        //assert found_tables == ['Person.Person'] : "Unexpected search results ${found_tables}"
        //println "test";
    }
}
