package xyz.ismailnurudeen.numsfact

import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        get_fact_btn.setOnClickListener {
            val qt = QueryTask(result_tv, progress_bar)

            val factNum = fact_num_input.text.toString()
            if (factNum.isNotEmpty()) {
                qt.execute(factNum)
            } else {
                Toast.makeText(this@MainActivity, "Enter a number...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun makeQuery(num: String): String {
        var queryResult: String
        try {
            val url = URL("http://numbersapi.com/$num")
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.connect()

            if (urlConnection.responseCode == 200) {
                val inputStream = urlConnection.inputStream
                val isr = InputStreamReader(inputStream, Charsets.UTF_8)
                val scanner = Scanner(isr)
                val sb = StringBuilder()
                while (scanner.hasNext()) {
                    sb.append(scanner.nextLine())
                }
                queryResult = sb.toString()
                inputStream.close()
            } else {
                queryResult = "No result found!"
                Log.i("RESPONSE_CODE", "URL:${url.path} CODE:${urlConnection.responseCode}")
            }
        } catch (e: Exception) {
            queryResult = "No result found!... Try again"
            runOnUiThread { Toast.makeText(this@MainActivity, e.localizedMessage, Toast.LENGTH_LONG).show() }
        }

        return queryResult
    }

    private inner class QueryTask(val resultTv: TextView, val pb: ProgressBar) : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            resultTv.visibility = View.GONE
            pb.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String): String {
            return makeQuery(params[0])
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            resultTv.text = result
            resultTv.visibility = View.VISIBLE
            pb.visibility = View.GONE
        }
    }
}
