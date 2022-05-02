package com.example.room_test001.database;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


//**    ****TableName****
//  Room預設使用類別名稱作為資料表名稱，若想要資料表使用不同名稱
//  可以設定@Entity註釋的tableName屬性。
//  ###資料表名稱是區分大小寫###
//
//  類似於 tableName的功能，Room 使用屬性名稱作為資料庫中的列名稱。如果希望列具有不同的名稱，請將 @ColumnInfo 註釋加到屬性中。
// /

@Entity(tableName = "MyTable01")  //這邊要先取好table的名字，稍後的table設置必須與他相同
public class MyData {

    //**    @PrimaryKey
    // 每個實體類別必須至少定義一個主鍵，即使只有一個屬性也須加上@PrimaryKey註釋來註釋該屬性
    // 若希望Room可以為實體自動分配ID，則可設定@PrimaryKey的autoGenerate屬性
    // /
    @PrimaryKey(autoGenerate = true)//設置是否使ID自動累加
    private int id;
    private String name;
    private String Age;
    private String Hobby;
    private String Email;

    public MyData(String name, String Age, String Hobby, String Email) {
        this.name = name;
        this.Age = Age;
        this.Hobby = Hobby;
        this.Email = Email;
    }
    //若實體具有不想被保存的屬性，則可使用@Ignore註釋
    @Ignore//如果要使用多形的建構子，必須加入@Ignore
    public MyData(int id, String name, String Age, String Hobby, String Email) {
        this.id = id;
        this.name = name;
        this.Age = Age;
        this.Hobby = Hobby;
        this.Email = Email;
    }
//**
// Getter-Setter 類別
// "在學習物件導向程式語言的時候，每個人一定都有寫到getter與setter的經驗。"
//
// getter-setter是用來作為物件的私有(private)變數或屬性(field)的公用存取介面(public access interface)，
// 也就是宣告兩個public method，
// 一個為getter用來取得private varible，
// 另一個則為setter用來設定private varible。
//
// 這樣的設計目的是為了避免private variable被不當使用，
// 像是可以在setter的設定時可加入一些條件判別或處理來避免變數被設成 unacceptable value。
//
// /
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String Age) {
        this.Age = Age;
    }

    public String getHobby() {
        return Hobby;
    }

    public void setHobby(String Hobby) {
        this.Hobby = Hobby;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }
}
