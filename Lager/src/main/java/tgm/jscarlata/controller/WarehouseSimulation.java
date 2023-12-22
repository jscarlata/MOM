package tgm.jscarlata.controller;

import tgm.jscarlata.model.Product;
import tgm.jscarlata.model.WarehouseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WarehouseSimulation {

  static String[] PRODUCT_NAMES = {"Smartphone", "Mobile Phone", "Tablet", "Laptop", "Personal Computer"};

  private double getRandomDouble( int inMinimum, int inMaximum ) {

    double number = ( Math.random() * ( (inMaximum-inMinimum) + 1 )) + inMinimum;
    double rounded = Math.round(number * 100.0) / 100.0;
    return rounded;

  }

  private int getRandomInt( int min, int max ) {

    Random random = new Random();
    return random.nextInt(max + 1 - min) + min;

  }

  public WarehouseData getData(String inID) {
    WarehouseData data = new WarehouseData();
    data.setWarehouseID("W001");
    data.setWarehouseName("Warehouse A");
    data.setWarehouseAddress("123 Main Street");
    data.setWarehousePostalCode("Wien");
    data.setWarehouseCity("Wien");
    data.setWarehouseCountry("Oesterreich");


    List<Product> products = new ArrayList<>();


    products.add(new Product("P001", PRODUCT_NAMES[getRandomInt(0,PRODUCT_NAMES.length - 1)], "Electronics", getRandomInt(5,80), "pieces"));
    products.add(new Product("P002", PRODUCT_NAMES[getRandomInt(0,PRODUCT_NAMES.length - 1)], "Electronics", getRandomInt(7,40), "pieces"));
    products.add(new Product("P003", PRODUCT_NAMES[getRandomInt(0,PRODUCT_NAMES.length - 1)], "Electronics", getRandomInt(2,130), "pieces"));
    products.add(new Product("P005", PRODUCT_NAMES[getRandomInt(0,PRODUCT_NAMES.length - 1)], "Electronics", getRandomInt(13,44), "pieces"));

    data.setProductData(products);


    return data;
  }

}
