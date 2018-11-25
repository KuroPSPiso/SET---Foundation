package com.capgemini.supermarket;

import com.capgemini.calendar.CalendarFormatting;
import com.capgemini.supermarket.payment.Currency;
import com.capgemini.supermarket.payment.IDiscountFormat;
import com.capgemini.supermarket.stock.Product;
import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main_Supermarket {
    public static final Product[] stock ={
            new Product("Robijn", new Currency(300), products -> {
                //conditional
                long discountValue = 0;
                int flaconDetection = 0;
                int foundRobijn = 0;
                Currency returnCurrency = Currency.Zero;
                Currency robijnCurrency = Currency.Zero;
                for(Product product : products)
                {
                    if(product.getName().toLowerCase().equals("flacon"))
                    {
                        flaconDetection += 1;
                    }
                    if(product.getName().toLowerCase().equals("robijn"))
                    {
                        robijnCurrency = product.getValue();
                        foundRobijn += 1;
                    }
                }

                if(foundRobijn > 0)
                {
                    for(;foundRobijn > 0; foundRobijn--)
                    {
                        if(flaconDetection >= 2)
                        {
                            returnCurrency.addValue(robijnCurrency.getPrecentage(31));
                            flaconDetection -= 2;
                        }
                    }
                }

                return returnCurrency;
            }),
            new Product("Brinta", new Currency(250)),
            new Product("Chinese Groenten", new Currency(500)),
            new Product("Kwark", new Currency(200)),
            new Product("Luiers", new Currency(1000)),
            new Product("Flacon", new Currency(130))
    };

    public static HashMap<Integer, Product> shoppingList;

    public static void main(String[] args) {
        //Data collection from io stream
        shoppingList = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        //Boolean to create a looping cycle
        boolean isRunning = true;
        while(isRunning)
        {
            //print question
            System.out.println("We currently have the following items in stock:\nItem no.\t|Item                |Price   |SALE");
            for(int i = 0; i < stock.length; i++)
            {
                Product p = stock[i];
                String name = p.getName();
                name = String.format("%s%"+ (20 - name.length()) + "s", name, "");
                System.out.println(String.format("%s\t\t\t|%s|%5d,%02d|", i, name, p.getValue().getValue(), p.getValue().getPrecision()));
            }
            System.out.println("Shopping cart:\n|Item                |price");
            if(shoppingList.size() > 0) {
                Currency totalCost = Currency.Zero;
                Currency totalCostReduction = Currency.Zero;
                Product[] cart = new Product[0];
                cart = shoppingList.values().toArray(cart);
                HashMap<String, Product> productHashMap = new HashMap<>();
                for (int i = 0; i < cart.length; i++) {
                    //add a every unique item in the shopping cart to a hashmap as each product scans the shopping list. (without this every discount can be counted multiple times)
                    if(!productHashMap.containsKey(cart[i].getName().toLowerCase()))
                        productHashMap.put(cart[i].getName().toLowerCase(), cart[i]);
                }
                for(Product p : productHashMap.values())
                {
                    for(IDiscountFormat idf : p.getDiscounts())
                    {
                        totalCostReduction.addValue(idf.calculateDiscount(cart));
                    }
                }

                System.out.println(String.format("Discount: %d,%02d", totalCostReduction.getValue(), totalCostReduction.getPrecision()));
                System.out.println(String.format("Total   : %d,%02d", totalCost.getValue(), totalCost.getPrecision()));
            }else{
                System.out.println("No items in shopping cart...");
            }

            System.out.println("write the following: 'add item', 'remove item', 'buy', or 'exit'");
            //get answer loop internally when failed
            failedReturn:
            for(;;) {
                String data = sc.nextLine();
                //expected result is either exit or a number so remove all spaces.
                //try to parse to a number, if it fails check if it's equal to exit else return.
                try {
                    parse(data);
                    break failedReturn;
                } catch (ParseFormatException pfe) {
                    //check for exit else return
                    if (data.equals("exit")) {
                        isRunning = false;
                        break failedReturn;
                    }
                    else {
                        System.out.println("write the following: 'add item', 'remove item', 'buy', or 'exit'");
                        continue failedReturn;
                    }
                }
            }
        }
    }

    public static void parse(String data) throws ParseFormatException {
        if(data.startsWith("add "))
        {
            String[] split = data.split(" ");
            if(split.length >= 2)
            {
                try {
                    int val = Integer.parseInt(split[1]);
                    if(val < stock.length && val >= 0)
                        shoppingList.put(val, stock[val]);
                    else
                        throw new Exception();
                }catch (Exception ex)
                {
                    System.out.println("Please enter number of a item you want to add to the cart.");
                }
            }
        }
        else if(data.startsWith("remove "))
        {
            String[] split = data.split(" ");
            try {
                int val = Integer.parseInt(split[1]);
                if(val < stock.length && val >= 0)
                    shoppingList.remove(val);
                else
                    throw new Exception();
            }catch (Exception ex)
            {
                System.out.println("Please enter number of a item you want to add to the cart.");
            }
        }
        else if(data.equals("buy"))
        {

        }
        else
        {
            throw new ParseFormatException();
        }
    }
}
