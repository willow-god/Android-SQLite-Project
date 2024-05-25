package com.example.exp2;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.exp2.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private EditText etUsername, etPassword, etPhone, etEmail;
    private String gender;

    private ArrayAdapter<String> spinnerAdapter;
    private Spinner spinner;
    private List<String> spinnerData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建DatabaseHelper的实例
        databaseHelper = new DatabaseHelper(this);

        // 调用getWritableDatabase()来获取一个用于写操作的数据库实例
        db = databaseHelper.getWritableDatabase();

        // 获取EditText控件的实例
        etUsername = findViewById(R.id.usernameEditText);
        etPassword = findViewById(R.id.passwordEditText);
        etPhone = findViewById(R.id.phoneEditText);
        etEmail = findViewById(R.id.emailEditText);
        gender = "未知"; // 初始化为未知或默认值
        spinner = findViewById(R.id.spinner);
        spinnerData = new ArrayList<>();
        // 填充 Spinner 数据
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COLUMN_USERNAME + " FROM " + DatabaseHelper.TABLE_USERS, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME));
            spinnerData.add(username);
        }
        cursor.close();
        db.close();
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerData);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 当选中某一项时，从数据库获取对应用户的所有信息
                String selectedUsername = spinnerData.get(position); // 获取选中的用户名
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                        null, // 选择所有列
                        DatabaseHelper.COLUMN_USERNAME + "=?",
                        new String[]{selectedUsername},
                        null, null, null);
                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD));
                    @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));
                    @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));
                    @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENDER));

                    // 更新输入框的值
                    etUsername.setText(selectedUsername);
                    etPassword.setText(password);
                    etPhone.setText(phone);
                    etEmail.setText(email);

                    // 根据性别设置单选按钮
                    if ("男".equals(gender)) {
                        ((RadioButton) findViewById(R.id.maleRadioButton)).setChecked(true);
                    } else {
                        ((RadioButton) findViewById(R.id.femaleRadioButton)).setChecked(true);
                    }
                }
                // 关闭游标和数据库
                cursor.close();
                db.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 清空输入框
                EditText etUsername = findViewById(R.id.usernameEditText);
                EditText etPassword = findViewById(R.id.passwordEditText);
                EditText etPhone = findViewById(R.id.phoneEditText);
                EditText etEmail = findViewById(R.id.emailEditText);

                etUsername.setText("");
                etPassword.setText("");
                etPhone.setText("");
                etEmail.setText("");

                // 取消选择单选按钮
                findViewById(R.id.maleRadioButton).setSelected(false);
                findViewById(R.id.femaleRadioButton).setSelected(false);
            }
        });

        // 获取 RadioGroup 和 RadioButton
        RadioGroup genderRadioGroup = findViewById(R.id.genderRadioGroup);
        RadioButton maleRadioButton = findViewById(R.id.maleRadioButton);
        RadioButton femaleRadioButton = findViewById(R.id.femaleRadioButton);

        // 为 RadioGroup 设置 OnCheckedChangeListener
        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // 根据选中的 RadioButton 更新 gender 变量
            if (checkedId == maleRadioButton.getId()) {
                gender = "男";
            } else if (checkedId == femaleRadioButton.getId()) {
                gender = "女";
            }
        });

        // 为添加按钮设置点击监听器
        findViewById(R.id.addButton).setOnClickListener(v -> {
            // 从输入框获取数据
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            // 检查输入是否为空
            if (username.isEmpty() || password.isEmpty() ||
                    phone.isEmpty() || email.isEmpty() || gender.equals("未知")) {
                Toast.makeText(MainActivity.this, "请填写所有字段", Toast.LENGTH_SHORT).show();
                return;
            }

            // 将数据放入ContentValues
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, username);
            values.put(DatabaseHelper.COLUMN_PASSWORD, password);
            values.put(DatabaseHelper.COLUMN_PHONE, phone);
            values.put(DatabaseHelper.COLUMN_EMAIL, email);
            values.put(DatabaseHelper.COLUMN_GENDER, gender);

            try {
                // 插入数据到数据库
                db = databaseHelper.getWritableDatabase();
                long newRowId = db.insert(DatabaseHelper.TABLE_USERS, null, values);
                if (newRowId != -1) {
                    Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    spinnerData.add(username);
                    spinnerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLiteConstraintException e) {
                Toast.makeText(this, "用户名已存在，请选择其他用户名", Toast.LENGTH_SHORT).show();
            }
        });

        // 保存按钮实现
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            // 获取输入框数据
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String genderDB = gender; // 假设gender变量已从界面获取

            if (username.isEmpty() || password.isEmpty() ||
                    phone.isEmpty() || email.isEmpty() || genderDB.equals("未知")) {
                Toast.makeText(MainActivity.this, "请填写所有字段", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查用户名是否已存在
            String selection = DatabaseHelper.COLUMN_USERNAME + "=?";
            String[] selectionArgs = new String[]{username};
            db = databaseHelper.getWritableDatabase();
            Cursor cursorsave = db.query(DatabaseHelper.TABLE_USERS,
                    new String[]{DatabaseHelper.COLUMN_ID}, // 只查询ID列
                    selection,
                    selectionArgs,
                    null, null, null);

            boolean userExists = cursorsave.getCount() > 0; // 检查游标是否有数据

            // 根据检查结果决定是添加新用户还是更新现有用户
            if (userExists) {
                // 用户存在，更新用户信息
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_PASSWORD, password);
                values.put(DatabaseHelper.COLUMN_PHONE, phone);
                values.put(DatabaseHelper.COLUMN_EMAIL, email);
                values.put(DatabaseHelper.COLUMN_GENDER, genderDB);

                int rowsAffected = db.update(DatabaseHelper.TABLE_USERS, values, selection, selectionArgs);
                if (rowsAffected > 0) {
                    Toast.makeText(MainActivity.this, "用户信息已更新", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "更新失败，用户未找到", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 用户不存在，提示先添加用户
                Toast.makeText(MainActivity.this, "用户不存在，请先添加用户", Toast.LENGTH_SHORT).show();
            }

            // 关闭游标
            cursor.close();
        });

        // 删除按钮实现
        findViewById(R.id.deleteButton).setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();

            // 检查用户名是否为空
            if (username.isEmpty()) {
                Toast.makeText(MainActivity.this, "请先选择要删除的用户", Toast.LENGTH_SHORT).show();
                return;
            }

            // 构建删除查询的选择语句和参数
            String selection = DatabaseHelper.COLUMN_USERNAME + "=?";
            String[] selectionArgs = new String[]{username};

            // 执行删除操作
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            int rowsDeleted = db.delete(DatabaseHelper.TABLE_USERS, selection, selectionArgs);

            if (rowsDeleted > 0) {
                Toast.makeText(MainActivity.this, "用户已删除", Toast.LENGTH_SHORT).show();
                spinnerData.remove(username);
                spinnerAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivity.this, "删除失败，用户未找到", Toast.LENGTH_SHORT).show();
            }

            // 清空输入框
            etUsername.setText("");
            etPassword.setText("");
            etPhone.setText("");
            etEmail.setText("");

            // 取消选择单选按钮
            findViewById(R.id.maleRadioButton).setSelected(false);
            findViewById(R.id.femaleRadioButton).setSelected(false);
        });

        // 清空按钮实现
        findViewById(R.id.clearButton).setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("清空操作")
                    .setMessage("请选择清空类型：")
                    .setPositiveButton("清空数据库数据", (dialog, which) -> {
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        int rowsDeleted = db.delete(DatabaseHelper.TABLE_USERS, null, null);
                        if (rowsDeleted > 0) {
                            Toast.makeText(MainActivity.this, "数据库已清空", Toast.LENGTH_SHORT).show();
                            // 清空界面输入框
                            etUsername.setText("");
                            etPassword.setText("");
                            etPhone.setText("");
                            etEmail.setText("");
                            findViewById(R.id.maleRadioButton).setSelected(false);
                            findViewById(R.id.femaleRadioButton).setSelected(false);
                            spinnerData.clear();
                            spinnerAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, "清空失败，数据库为空", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("清空界面输入框", (dialog, which) -> {
                        // 用户选择仅清空界面输入框
                        etUsername.setText("");
                        etPassword.setText("");
                        etPhone.setText("");
                        etEmail.setText("");

                        findViewById(R.id.maleRadioButton).setSelected(false);
                        findViewById(R.id.femaleRadioButton).setSelected(false);
                        Toast.makeText(MainActivity.this, "界面已清空", Toast.LENGTH_SHORT).show();
                    })
                    .setOnCancelListener(dialog -> {})
                    .create()
                    .show();
        });
    }
}