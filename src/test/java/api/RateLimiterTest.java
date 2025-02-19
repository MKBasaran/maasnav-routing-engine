package api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.group8.phase1.api.RateLimiter;

class RateLimiterTest {

    private static File rateLimitFile;
    private RateLimiter rateLimiter;
    int[] maxRequests = {5, 10, 20, 50};

    @BeforeEach
    public void setUp() {
        try {
            rateLimitFile = Paths.get(this.getClass().getResource("rateLimitData.txt").toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        rateLimiter = new RateLimiter(maxRequests,true);
    }


    @AfterEach
    void tearDown() {
        try {
                FileWriter writer = new FileWriter(rateLimitFile);
                writer.write("");
                writer.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

    }

    @Test
    void allowRequest() {
        for(int i=0;i<30;i++){
            if(i>=maxRequests[0]){
                assertFalse(rateLimiter.allowRequest());
            }else{
                assertTrue(rateLimiter.allowRequest());
            }
        }

    }
}