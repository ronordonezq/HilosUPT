package com.example.hilosupt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity  extends AppCompatActivity {
  private EditText entrada;
  private TextView salida;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    entrada = (EditText) findViewById(R.id.entrada);
    salida = (TextView) findViewById(R.id.salida);
  }
  public void calcularOperacion(View view) {
    int n = Integer.parseInt(entrada.getText().toString());
    salida.append(n + "! = ");

    //SIN HILOS
    /*int res = factorial(n);
    salida.append(res + "\n");*/

    //CON THREAD
    /*MiThread thread = new MiThread(n);// SE EJECUTA EL CONSTRUCTOR MiThread
    thread.start(); //SE EJECUTA run*/

    //CON ASYNCTASK
    MiTarea tarea = new MiTarea();
    tarea.execute(n);

  }
  public int factorial(int n) {
    int res = 1;
    for (int i = 1; i <= n; i++) {
      res *= i;
      SystemClock.sleep(1000);
    }
    return res;
  }

  class MiThread extends Thread {
    private int n, res;
    public MiThread(int n) {
      this.n = n;
    }
    @Override public void run() {
      res = factorial(n);
      runOnUiThread(new Runnable() {
        @Override public void run() {
          salida.append(res + "\n");
        }
      });
    }
  }


  class MiTarea extends AsyncTask<Integer, Integer, Integer> {
    private ProgressDialog progreso;
    @Override protected void onPreExecute() {
      progreso = new ProgressDialog(MainActivity.this);
      progreso.setProgressStyle(ProgressDialog.
              STYLE_HORIZONTAL);
      progreso.setMessage("Calculando...");
      //progreso.setCancelable(false);
      progreso.setMax(100);
      progreso.setProgress(0);
      progreso.setCancelable(true);
      progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
          MiTarea.this.cancel(true);
        }
      });


      progreso.show();
    }
    @Override protected Integer doInBackground(Integer... n) {
      int res = 1;
      for (int i = 1; i <= n[0] && !isCancelled(); i++) {
        res *= i;
        SystemClock.sleep(1000);
        publishProgress(i*100 / n[0]);
      }
      return res;
    }
    @Override protected void onProgressUpdate(Integer... porc) {
      progreso.setProgress(porc[0]);
    }
    @Override protected void onPostExecute(Integer res) {
      progreso.dismiss();
      salida.append(res + "\n");
    }
    @Override protected void onCancelled() {
      salida.append("cancelado\n");
    }
  }
}
