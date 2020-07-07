package com.example.bus_timetable_retrofit_kotlin_application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var characterAdapter: CharacterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch to AppTheme for displaying the activity
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Your code
        characterAdapter = CharacterAdapter()
        character_recyclerview.adapter = characterAdapter

        val potterApi = Retrofit.Builder()
            //.baseUrl("https://841c22f8-d56a-470c-ab09-60de31bebc40.mock.pstmn.io/")
            .baseUrl((application as BusApp).getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .client(OkHttpProvider.getOkHttpClient())
            .build()
            .create(BusApi::class.java)

        potterApi.getCharacters().enqueue(object : Callback<List<CharacterModel>> {
            override fun onFailure(call: Call<List<CharacterModel>>, t: Throwable) {
                showErrorState()
            }

            override fun onResponse(call: Call<List<CharacterModel>>,
                                    response: Response<List<CharacterModel>>) {
                if (response.isSuccessful && response.body() != null) {
                    val characterList = response.body()!!
                    if (characterList.isEmpty()) {
                        showEmptyDataState()
                    } else {
                        showCharacterList(characterList)
                    }
                } else {
                    showErrorState()
                }
            }
        })
    }

    private fun showEmptyDataState() {
        character_recyclerview.visibility = View.GONE
        progress_bar.visibility = View.GONE
        textview.visibility = View.VISIBLE
        textview.text = getString(R.string.there_seems_to_be_no_data)
    }

    private fun showCharacterList(characterList: List<CharacterModel>) {
        character_recyclerview.visibility = View.VISIBLE
        progress_bar.visibility = View.GONE
        textview.visibility = View.GONE
        characterAdapter.setCharacterList(characterList)
    }

    private fun showErrorState() {
        character_recyclerview.visibility = View.GONE
        progress_bar.visibility = View.GONE
        textview.visibility = View.VISIBLE
        textview.text = getString(R.string.something_went_wrong)
    }

}
