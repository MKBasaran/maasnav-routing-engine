package database;

import com.group8.phase1.database.Setup;

import com.group8.phase1.settings.DATABASE_CONFIG;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
public class DatabaseTest {

    @Test
    public void testCreateNewDatabase() {
        File dbFile = new File(DATABASE_CONFIG.DATABASE_URL.substring(12));
        if(dbFile.exists())
            dbFile.delete();

        Setup.createNewDatabase();
        dbFile = new File(DATABASE_CONFIG.DATABASE_URL.substring(12));
        assertTrue(dbFile.exists());
        dbFile.delete();
    }
}
