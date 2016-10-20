package android.triadworks.cursos.triadphoto;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CameraActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private Button btn;
    private ImageView img;

    private String caminhoFoto;

    private static final int CODIGO_GALERIA = 100;
    private static final int CODIGO_CAMERA = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        img = (ImageView) findViewById(R.id.foto);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(CameraActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(CameraActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                }else{
                    registerForContextMenu(btn);
                    openContextMenu(btn);
                }

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuItem carregarImg = menu.add("da Galeria");
        carregarImg.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selecionarGaleria();
                return false;
            }
        });

        MenuItem camera = menu.add("da Câmera");
        camera.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selecionarCamera();
                return false;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_GALERIA && resultCode == RESULT_OK) {
            carregarImagemGaleria(data);
        }


        if(requestCode == CODIGO_CAMERA && resultCode == RESULT_OK){
            carregarImagemCamera();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    public void selecionarGaleria(){
        Intent abrirGaleria = new Intent(Intent.ACTION_GET_CONTENT);
        abrirGaleria.setType("image/*");
        abrirGaleria.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(abrirGaleria,CODIGO_GALERIA);
    }

    public void selecionarCamera(){
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        caminhoFoto = getExternalFilesDir(null) + "/"
                + System.currentTimeMillis() + ".jpg";
        File arquivoFoto = new File(caminhoFoto);

        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto));
        startActivityForResult(intentCamera, CODIGO_CAMERA);
    }

    public void carregarImagemGaleria(Intent data){
        InputStream stream = null;

        try {
            if(bitmap != null){
                bitmap.recycle();
            }

            stream = getContentResolver().openInputStream(data.getData());
            bitmap = BitmapFactory.decodeStream(stream);
            img.setImageBitmap(bitmap);
            exibirMsg("Imagem da GALERIA carregada...");
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    public void carregarImagemCamera(){
        Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto);
        Bitmap bitmapReduzido = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

        img.setImageBitmap(bitmapReduzido);
        img.setScaleType(ImageView.ScaleType.FIT_XY);

        img.setTag(caminhoFoto);
        exibirMsg("Imagem da CÂMERA carregada...");
    }

    public void exibirMsg(String msg){
        View viewById = findViewById(R.id.content_camera);
        Snackbar.make(viewById, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
