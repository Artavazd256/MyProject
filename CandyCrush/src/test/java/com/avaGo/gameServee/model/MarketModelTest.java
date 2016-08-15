package com.avaGo.gameServee.model;

import org.bson.Document;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by artavzd on 8/15/16.
 */
public class MarketModelTest {
    @Test
    public void getProductByName() throws Exception {
        Document buster = MarketModel.getProductByName("buster1");
        assertNotNull(buster);
    }

    @Test
    public void getAllMarket() throws Exception {
        List<Document> allMarket = MarketModel.getAllMarket();
        assertTrue(allMarket.size() != 0 ? true : false);
    }

}