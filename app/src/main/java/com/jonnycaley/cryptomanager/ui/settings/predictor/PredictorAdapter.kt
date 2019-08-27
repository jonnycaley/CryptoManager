package com.jonnycaley.cryptomanager.ui.settings.predictor

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.Predictor.Layer
import kotlinx.android.synthetic.main.item_predictor.view.*
import kotlinx.android.synthetic.main.item_transaction_history.view.*

class PredictorAdapter(val layers: MutableList<Layer>, val context: Context?) : RecyclerView.Adapter<PredictorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_predictor, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layer = layers[position]

        when(layer.className){
            "LSTM" -> {
                holder.type.text = "LSTM"

                holder.units.text = "Units - ${layer.config.units}" //128
                holder.units.visibility = View.VISIBLE

                holder.shape.text = "Recurrent Activation - ${layer.config.recurrentActivation}" //hard_sigmoid
                holder.shape.visibility = View.VISIBLE

                holder.return_sequences.text = "Return Sequences - ${layer.config.returnSequences}" //boolean
                holder.return_sequences.visibility = View.VISIBLE

                holder.prediction_type.setBackgroundResource(R.drawable.button_checked)
            }
            "Dropout" -> {
                holder.type.text = "Dropout"

                holder.units.text = "Rate - ${layer.config.rate}"
                holder.units.visibility = View.VISIBLE

                holder.prediction_type.setBackgroundResource(R.drawable.button_checked_green)

            }
            "BatchNormalizationV1" -> {
                holder.type.text = "Batch Normalization"
                holder.prediction_type.setBackgroundResource(R.drawable.button_checked_red)
            }
            "Dense" -> {
                holder.type.text = "Dense"

                holder.units.text = "Units - ${layer.config.units}"
                holder.units.visibility = View.VISIBLE

                holder.shape.text = "Activation - ${layer.config.activation}"
                holder.shape.visibility = View.VISIBLE
                holder.prediction_type.setBackgroundResource(R.drawable.button_checked_yellow)
            }
        }
    }

    override fun getItemCount(): Int {
        return layers.size
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val type = view.type
        val units = view.units
        val shape = view.shape
        val return_sequences = view.return_sequences

        val prediction_type = view.prediction_type
    }
}