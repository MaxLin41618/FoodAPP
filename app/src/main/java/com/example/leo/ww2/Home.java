package com.example.leo.ww2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Interface.ItemClickListener;
import com.example.leo.ww2.Model.Billboard;
import com.example.leo.ww2.Model.Store;
import com.example.leo.ww2.Model.Token;
import com.example.leo.ww2.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Home";
    //Firebase資料庫
    FirebaseDatabase database;
    DatabaseReference refStore, refBillboardTbl;

    //Storage圖檔 資料
    FirebaseStorage storage;
    StorageReference storageReference;

    //FireBase UI
    FirebaseRecyclerAdapter<Store, MenuViewHolder> adapter;
    //Uri  for-> onActivityResult() , uploadImage()
    Uri saveImgUri;

    Billboard newBillboard;

    RecyclerView recycler_menu;
    TextView txtFullName;
    EditText edtTitle, edtComment;
    Button btnUpload, btnSelect;
    ImageView imageView;
    FloatingActionButton fabCart, fabBillBoard;

    //for back press
    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Init FireBase
        database = FirebaseDatabase.getInstance();
        refStore = database.getReference("Store");
        refBillboardTbl = database.getReference("Billboard");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //Init Paper (remember user)
        Paper.init(this);

        initView();

        //確認網路連線
        if (Common.isConnectedToInternet(this)) {
            loadMenu();
        } else {
            Toast.makeText(Home.this, "請確認網路連線", Toast.LENGTH_SHORT).show();
        }

        //create or update Token
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void initView() {
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Fab
        fabCart = (FloatingActionButton) findViewById(R.id.fab);
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        fabBillBoard = (FloatingActionButton) findViewById(R.id.fab2);
        fabBillBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBillboardDialog();
            }
        });

        //側滑選單
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //SetNameForUser nav_header
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

        //LoadMenu RecyclerView
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        recycler_menu.setLayoutManager(new LinearLayoutManager(this));
    }

    //新增Token
    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token tokenData = new Token(token, false);
        tokens.child(Common.currentUser.getPhone()).setValue(tokenData);
    }

    private void showBillboardDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("發布公告");
        alertDialog.setMessage("請輸入資訊");
        alertDialog.setIcon(R.drawable.ic_chat_black_24dp);

        //找 layout
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_billboard_layout = inflater.inflate(R.layout.add_new_billboard_layout, null);
        alertDialog.setView(add_new_billboard_layout);

        edtTitle = add_new_billboard_layout.findViewById(R.id.edtTitle);
        edtComment = add_new_billboard_layout.findViewById(R.id.edtComment);
        btnUpload = add_new_billboard_layout.findViewById(R.id.btnUpload);
        btnSelect = add_new_billboard_layout.findViewById(R.id.btnSelect);
        imageView = add_new_billboard_layout.findViewById(R.id.image);

        //Button Event
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                //Submit to FireBase
                if (newBillboard != null) {
                    refBillboardTbl.child(String.valueOf(System.currentTimeMillis())).setValue(newBillboard);
                    Toast.makeText(Home.this, "已發布公告!!", Toast.LENGTH_SHORT).show();
                }
                //一直新增uni-key
           /* BillboardTbl.push()
                    .setValue(newBillboard)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Home.this,"已發布公告!!",Toast.LENGTH_SHORT).show();
                        }
                    });*/
            }
        });
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show(); //show Dialog
    }

    private void loadMenu() {
        FirebaseRecyclerOptions<Store> options = new FirebaseRecyclerOptions.Builder<Store>().setQuery(refStore, Store.class).build();
        adapter = new FirebaseRecyclerAdapter<com.example.leo.ww2.Model.Store, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Store model) {

                holder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.imageView);

                final Store clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //店名傳遞Key值
                        Intent foodList = new Intent(Home.this, FoodList.class);
                        foodList.putExtra("StoreId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.menu_item, parent, false);

                return new MenuViewHolder(view);
            }
        };
        adapter.startListening();
        recycler_menu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                //Exit app
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast mExitToast = new Toast(this);
            mExitToast.makeText(this, "再按一次返回鍵離開", Toast.LENGTH_SHORT).show();
            mExitToast.cancel();
            mHandler.postDelayed(mRunnable, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refreshList) {
            loadMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);
        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_log_out) {
            //Delete Remembered user_phone & password
            Paper.book().destroy();

            //Logout addFlags清理疊加的Activity
            Intent signIn = new Intent(Home.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signIn);
        } else if (id == R.id.nav_billboard) {
            Intent billboardIntent = new Intent(Home.this, ShowBillboard.class);
            billboardIntent.putExtra("UserPhone", Common.currentUser.getPhone());
            billboardIntent.putExtra("UserName", Common.currentUser.getName());
            startActivity(billboardIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //圖片相關
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    //圖片相關
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveImgUri = data.getData();
            btnSelect.setText("圖片選擇成功!!");
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), saveImgUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //圖片相關
    private void uploadImage() {
        if (saveImgUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("上傳中...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference refImageFolder = storageReference.child("images/" + imageName);
            UploadTask uploadTask = refImageFolder.putFile(saveImgUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(Home.this, "上傳成功!!", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("上傳中" + progress + "%");
                        }
                    });

            //get downloadUrl for put it in a Billboard object
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return refImageFolder.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        newBillboard = new Billboard(
                                downloadUri.toString(),
                                Common.currentUser.getName(),
                                Common.currentUser.getPhone(),
                                edtTitle.getText().toString(),
                                edtComment.getText().toString());
                    } else {
                        // Handle failures
                    }
                }
            });
        }
    }

    // 在返回這頁時發生空白，用這個補回資料
    @Override
    public void onResume() {
        super.onResume();

        loadMenu();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
}
