package com.group8.phase1.osm.checker;

import com.group8.phase1.database.Setup;
import com.group8.phase1.database.SetupAccess;
import com.group8.phase1.initialdataloader.ProgressPanel;
import com.group8.phase1.osm.setup.LoadDataFromPBF;
import com.group8.phase1.settings.DATABASE_CONFIG;
import com.group8.phase1.sociometric.parser.*;

import java.io.File;

public enum CheckDatabase {
    ;

    public static void check(ProgressPanel progressViewer) {
        File file = new File(DATABASE_CONFIG.DATABASE_URL.substring(12));
        if (!file.exists()) {
            Setup.setup(progressViewer);
            LoadDataFromPBF.loadPBF(progressViewer);
        }
        File fileAccess = new File(DATABASE_CONFIG.ACCESSDB_URL.substring(12));
        if (!fileAccess.exists()){
            SetupAccess.setupAccess(progressViewer);
            ScoreAndMultiplier.performInsert();
            GeoJSONToMySQLAmenities.insertAmenitiesData();
            GeoJSONToMySQLShops.insertShopsData();
            GeoJSONToMySQLTourism.insertTourismData();
            XLSXtoTransport.readXLSXFile();
        }
    }
}
