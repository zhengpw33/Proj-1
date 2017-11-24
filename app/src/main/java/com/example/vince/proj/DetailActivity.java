package com.example.vince.proj;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vince.proj.DB.Role;
import com.example.vince.proj.UI.CardScaleHelper;
import com.example.vince.proj.UI.RoleAdapter;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private RecyclerView rolesView;
    private List<Role> roles = new ArrayList<>();
    private CardScaleHelper cardScaleHelper = null;
    private Runnable runnable;
    private boolean showMaskingAdd = false;
    private boolean showMaskingSearch = false;
    private RoleAdapter roleAdapter = new RoleAdapter(roles);
    private View masking_add;
    private View masking_search;
    public static final int TAKE_PHOTO = 1;//启动相机标识
    public static final int SELECT_PHOTO = 2;//启动相册标识
    private ImageView imageView;
    private File outputImagepath;//存储拍完照后的图片的路径
    private Bitmap orc_bitmap;//拍照和相册获取图片的Bitmap
    private HashMap<String, Integer> nameToId = new HashMap<String, Integer>();
    //创建文件夹
    String Picture;
    String Music ;
    Context context;

    private EditText newRoleName;
    private EditText newRoleNativePlace;
    private String   newRoleImagePath;


    //private String[] mRolesNameList = {};
    private ArrayList<String>mRolesNameList = new ArrayList<>();
    private SearchView mSearchView;
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        masking_add = findViewById(R.id.masking_add);
        masking_add.setVisibility(View.GONE);
        masking_search = findViewById(R.id.masking_search);
        masking_search.setVisibility(View.GONE);

        Picture = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        Music = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        context = getApplicationContext();
        imageView =(ImageView)findViewById(R.id.portrait_masking_add);//不能用

        roleAdapter.setOnItemClickListener(new RoleAdapter.OnItemClickListener() {
            @Override
            public void onLongClick(int position) {
                roleAdapter.removeItem(position);
                roles.remove(position);
                //DataSupport.delete(Role.class, roles.get(position).getId());
                DataSupport.deleteAll(Role.class, "name = ?", roles.get(position).getName());
            }
        });
        init();

        //Connector.getDatabase();

        //List<Role>rolesFromQuery = DataSupport.select("name").find(Role.class);
        for(int i = 0; i < roles.size(); i++){
            mRolesNameList.add( roles.get(i).getName());
        }

        /****************************添加功能****************************/
        //绑定添加按钮
        newRoleName = (EditText) findViewById(R.id.name_masking_add);
        newRoleNativePlace = (EditText)findViewById(R.id.native_place_masking_add);

        final com.getbase.floatingactionbutton.FloatingActionButton fab_add = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //View masking_add = findViewById(R.id.masking_add);
                showMaskingAdd = !showMaskingAdd;
                if(showMaskingAdd){
                    masking_add.setVisibility(View.VISIBLE);
                }
                else{
                    masking_add.setVisibility(View.GONE);
                }
                //设置fab_menu按钮折叠
                com.getbase.floatingactionbutton.FloatingActionsMenu fab_menu = (com.getbase.floatingactionbutton.FloatingActionsMenu) findViewById(R.id.fab_menu);
                fab_menu.collapseImmediately();
            }
        });

        //添加中的确定按钮
        Button commit_masking_add = (Button) findViewById(R.id.commit_masking_add);
        commit_masking_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Connector.getDatabase();
                int Index = roles.size();
                Role role_ = new Role();
                role_.setId(Index++);
                role_.setName(newRoleName.getText().toString());
                role_.setImageId(R.mipmap.caocao);
                role_.setLifeTime("? - ?");
                role_.setDefault(false);
                role_.setImagePath(newRoleImagePath);
                //Log.i(TAG, "initRoleData: "+role_.getLifeTime());
                role_.setNationality("魏");
                role_.setDescription("Test");
                role_.save();
                roles.add(role_);
                roleAdapter.notifyDataSetChanged();
                //roleAdapter.notifyItemInserted(Index);
                masking_add.setVisibility(View.GONE);
                mRolesNameList.add(role_.getName());
            }
        });

        Button cancel_masking_add = (Button) findViewById(R.id.cancel_masking_add);
        cancel_masking_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMaskingAdd = false;
                masking_add.setVisibility(View.GONE);
            }
        });

        final String[] way = new String[]{"拍摄","从相册选择"};
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog.Builder dialog;
                dialog = new AlertDialog.Builder(DetailActivity.this);
                dialog.setTitle("").setItems(way, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            xiangjiClick(v);
                        }
                        if(which==1){
                            xiangceClick(v);
                        }
                    }
                }).create().show();
            }
        });

        /****************************添加功能****************************/



        /****************************搜索功能****************************/

        //绑定搜索按钮
        com.getbase.floatingactionbutton.FloatingActionButton fab_search = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_search);
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //View masking_search = findViewById(R.id.masking_search);
                showMaskingSearch = !showMaskingSearch;
                if(showMaskingSearch){
                    masking_search.setVisibility(View.VISIBLE);
                }
                else{
                    masking_search.setVisibility(View.GONE);
                }

                //设置fab_menu按钮折叠
                com.getbase.floatingactionbutton.FloatingActionsMenu fab_menu = (com.getbase.floatingactionbutton.FloatingActionsMenu) findViewById(R.id.fab_menu);
                fab_menu.collapseImmediately();
            }
        });


        mSearchView = (SearchView) findViewById(R.id.search_view_masking_search);

        mListView  = (ListView) findViewById(R.id.list_view_masking_search);
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mRolesNameList));
        mListView.setTextFilterEnabled(true);

        //ListView添加监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cardScaleHelper.setCurrentItemPos((int)id);
                showMaskingSearch = false;
                String role_name_ = (String)parent.getItemAtPosition(position);
                rolesView.smoothScrollToPosition(nameToId.get(role_name_)-1);
                Log.i(TAG, "onItemClick: "+nameToId.get(role_name_));
                masking_search.setVisibility(View.GONE);
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    mListView.setFilterText(newText);
                }else{
                    mListView.clearTextFilter();
                }
                return false;
            }
        });

        /****************************搜索功能****************************/
    }

    //初始化RecyclerView
    private void init(){
        List<Role> roles_ = DataSupport.findAll(Role.class);

        roles.clear();
        for(Role role: roles_){
            roles.add(role);
            nameToId.put(role.getName(), role.getId());
        }
        Log.i(TAG, "init: "+roles.size());
        rolesView = (RecyclerView)findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rolesView.setLayoutManager(linearLayoutManager);
        rolesView.setAdapter(roleAdapter);
        cardScaleHelper = new CardScaleHelper();
        cardScaleHelper.setCurrentItemPos(2);
        cardScaleHelper.attachToRecyclerView(rolesView);
    }


    /**
     * 打开相机
     */
    public void xiangjiClick(View view) {
        //checkSelfPermission 检测有没有 权限，PackageManager.PERMISSION_GRANTED 有权限，PackageManager.PERMISSION_DENIED  拒绝权限，一定要先判断权限,再打开相机,否则会报错
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        else {
            try {
                take_photo();//已经授权了就调用打开相机的方法
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 拍照获取图片
     **/
    public void take_photo() throws ClassNotFoundException {
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if ( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            outputImagepath = new File(Picture, filename + ".jpg");
            // 从文件中创建uri
            Uri uri = Uri.fromFile(outputImagepath);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, TAKE_PHOTO);
    }
    /**
     * 打开相册
     */
    public void xiangceClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {/*打开相册*/
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    /*这种方法是通过内存卡的路径进行读取图片，所以的到的图片是拍摄的原图*/
                    displayImage(outputImagepath.getAbsolutePath());
                    Log.i("tag", "拍照图片路径>>>>" + outputImagepath);
                }
                break;
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    handleImgeOnKitKat(data);
                    try {
                        saveFile(orc_bitmap,"name.jpg",Picture,context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImgeOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("uri=intent.getData :", "" + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);        //数据表里指定的行
            Log.d("getDocumentId(uri) :", "" + docId);
            Log.d("uri.getAuthority() :", "" + uri.getAuthority());
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        }
        displayImage(imagePath);
    }
    /**
     * 拍完照和从相册获取玩图片都要执行的方法(根据图片路径显示图片)
     */
    private void displayImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            Log.i(TAG, "displayImage: "+imagePath);

            newRoleImagePath = imagePath;
            orc_bitmap = BitmapFactory.decodeFile(imagePath);//获取图片 // orc_bitmap = comp(BitmapFactory.decodeFile(imagePath)); //压缩图
            imageView.setImageBitmap(orc_bitmap);

        } else {
            Toast.makeText(this, "图片获取失败", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 通过uri和selection来获取真实的图片路径,从相册获取图片时要用
     */
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public static void saveFile(Bitmap bm, String filename, String path, Context context) throws IOException {
        String subForder = path;
        Log.i("cunchulujing", subForder);
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(subForder, filename);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
        bos.flush();
        bos.close();
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(foder);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
}
