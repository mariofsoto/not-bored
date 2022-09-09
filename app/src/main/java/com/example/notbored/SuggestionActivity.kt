package com.example.notbored

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.notbored.databinding.ActivitySuggestionBinding
import java.util.*

class SuggestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuggestionBinding
    private val repository = Repository()
    private var isRandom = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuggestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        val participants = Utils.getSharedValue(this,BoredPreferences.PARTICIPANTS) as Int
        val maxPrice = Utils.getSharedValue(this,BoredPreferences.MAX_PRICE) as Float
        val minPrice = Utils.getSharedValue(this,BoredPreferences.MIN_PRICE) as Float

        val dataFromLastActivity = intent.extras
        val type =  dataFromLastActivity?.getString("type","") ?: ""
        isRandom = type == "random"

        observeEvent(type.lowercase(),participants,minPrice,maxPrice)
        Log.d(TAG, "onSuggestion: calling api with type:$type, participants: $participants," +
            " price ($minPrice, $maxPrice)")

        binding.topAppBar.title = type.boredCapitalize()

        binding.btnRetry.setOnClickListener {
            observeEvent(type.lowercase(),participants,minPrice,maxPrice)
        }
    }
    private fun updateViews(binding: ActivitySuggestionBinding, event : BoredEvent){
        binding.tvActivityLabel.text = event.activity
        binding.tvParticipantsCount.text = event.participants.toString()
        binding.tvPrice.text = getPrice(event.price)
        if(isRandom){
            binding.tvCategory.visibility =  View.VISIBLE
            binding.tvCategory.text =  event.type.boredCapitalize()
        }

    }

    private fun observeEvent(type:String, participants:Int, minPrice : Float, maxPrice : Float){
        val event = repository.getBoredEvent(type,participants,minPrice,maxPrice)
        event.observe(this){
            updateViews(binding,it)
        }
    }
    private fun getPrice(price: Float): String = when {
        price == 0.0f -> "Free"
        price > 0.0f && price <= 0.3f  -> "Low"
        price > 0.3f && price <= 0.6f  -> "Medium"
        else -> "High"
    }
    private fun String.boredCapitalize():String{
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }
}
