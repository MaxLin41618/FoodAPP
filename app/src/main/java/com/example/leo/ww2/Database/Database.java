package com.example.leo.ww2.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.leo.ww2.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    //Remember SQLiteAssetHelper must have a asset file in "assets/database/"
    //These will include the initial SQLite database file for creation and optionally any SQL upgrade scripts.
    //Rather than bunch of SQL statements

    //DB_NAME
    private static final String DB_NAME = "Eat.db";
    //version
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    //display ALL
    public List<Order> getCarts() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProductName", "ProductId", "Quantity", "Price"};
        //TABLE_NAME
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<Order> orderList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                orderList.add(new Order(
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price"))
                ));
            } while (c.moveToNext());
        }
        return orderList;
    }

    //add one Order
    public void addToCart(Order order) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("INSERT INTO OrderDetail (ProductId,ProductName,Quantity,Price)  VALUES('%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice()
        );
        db.execSQL(query);
    }

    //clean ALL
    public void cleanCart() {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }
}
