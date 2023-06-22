package com.example.todolistapp.adapter


import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.todolistapp.R
import com.example.todolistapp.interfaces.celebratable
import com.example.todolistapp.model.Task



class TasksAdapter(private val rv: RecyclerView,private val context: Context, private var dataSet: ArrayList<Task>, private val celebratable: celebratable) :
    RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    private var tasksDoneCount = 0

    init {
        tasksDoneCount=0
        for(t in dataSet) if(t.status) tasksDoneCount++
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitle: TextView
        val taskPriority: TextView
        val tick: ImageView
        val delete: ImageView

        init {
            // Define click listener for the ViewHolder's View
            taskTitle = view.findViewById(R.id.rv_tasktitle)
            taskPriority = view.findViewById(R.id.rv_taskPriority)
            tick = view.findViewById(R.id.imgTick)
            delete = view.findViewById(R.id.rv_delete)
            tick.setOnClickListener {
                doneTask(view)
            }
            delete.setOnClickListener {
                deleteTask(view)
            }

        }
    }


    private fun cancelTasks(textView:TextView){
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    private fun disableCancel(textView: TextView){
//        textView.setPaintFlags(textView.paintFlags and (Paint.STRIKE_THRU_TEXT_FLAG))
        textView.paintFlags = 0
        textView.setTypeface(null, Typeface.BOLD);
    }



    fun deleteTask(view:View){
        val pos = rv.getChildLayoutPosition(view)
        val taskToBeDeleted = dataSet[pos]
        val mp = MediaPlayer.create(context, R.raw.delete)
        mp.start()
        dataSet.removeAt(pos)
        rv.adapter = TasksAdapter(rv,context,dataSet,celebratable)



    }

    fun doneTask(view: View){
        val pos = rv.getChildLayoutPosition(view)
        var status = dataSet[pos].status
        val imgView = view.findViewById<ImageView>(R.id.imgTick)
        if(status){
            view.findViewById<TextView>(R.id.rv_tasktitle).setTextColor(Color.parseColor("#000000"))
            Glide.with(context).load(R.drawable.tick_inactive).into(imgView)
            disableCancel(view.findViewById(R.id.rv_tasktitle))
            tasksDoneCount -= 1
        }
        else{
            view.findViewById<TextView>(R.id.rv_tasktitle).setTextColor(Color.parseColor("#7F8C8D"))
            Glide.with(context).load(R.drawable.tick_active).into(imgView)
            cancelTasks(view.findViewById(R.id.rv_tasktitle))
            val mp = MediaPlayer.create(context, R.raw.success2)
            mp.start()
            tasksDoneCount += 1
            if(tasksDoneCount == dataSet.size){
                congratsAllTaskDone()
            }
        }
        dataSet[pos].status = !dataSet[pos].status
//        rv.adapter!!.notifyItemChanged(pos)
        rv.adapter!!.notifyDataSetChanged()


    }

    private fun congratsAllTaskDone(){
        Toast.makeText(context, "Congrats All tasks done", Toast.LENGTH_SHORT).show()
        celebratable.celebrate()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.rv_task_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val priority = dataSet[position].priority
        viewHolder.taskTitle.text = dataSet[position].title
        viewHolder.taskPriority.text = priority
        if(priority=="High"){
            viewHolder.taskPriority.setTextColor(Color.parseColor("#FF0000"))
        }
        else if(priority=="Medium"){
            viewHolder.taskPriority.setTextColor(Color.parseColor("#FFA500"))
        }
        else{
           viewHolder.taskPriority.setTextColor(Color.parseColor("#228B22"))
        }
        if(!dataSet[position].status){
            Glide.with(context).load(R.drawable.tick_inactive).into(viewHolder.tick)
            disableCancel(viewHolder.taskTitle)
        }
        else{
            Glide.with(context).load(R.drawable.tick_active).into(viewHolder.tick)
            cancelTasks(viewHolder.taskTitle)
        }



    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
