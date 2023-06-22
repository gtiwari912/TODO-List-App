package com.example.todolistapp

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.adapter.TasksAdapter
import com.example.todolistapp.constants.Constants
import com.example.todolistapp.interfaces.celebratable
import com.example.todolistapp.model.Task
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), celebratable {

    private lateinit var taskSpinner: Spinner
    private lateinit var toolbar: Toolbar
    private lateinit var btnAddTask:Button
    private lateinit var rv:RecyclerView
    private lateinit var taskList:ArrayList<Task>
    private lateinit var etEnterTask: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initVariables()
        initViews()
        setUpSpinner()
        setSupportActionBar(toolbar)
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)
        setUpRv()
        btnAddTask.setOnClickListener {
            addTask()
        }

    }

    private fun setUpRv(){
        if(taskList.isEmpty() && sharedPreferences.contains("tasks")){
            val tasksInStr = sharedPreferences.getString("tasks","")
            taskList = convertJsonStringToList(tasksInStr!!)
        }
        rv.adapter = TasksAdapter(rv,this@MainActivity, taskList,this)
        rv.layoutManager = LinearLayoutManager(this)
    }

    private fun initVariables(){
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        taskList = ArrayList()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this, "onBackPressed Called",Toast.LENGTH_SHORT).show()
        editor.apply {
            clear()
            apply()
        }
        if(taskList.size > 0){
            val listInStringFormat = convertListToString(taskList)
            editor.apply {
                putString("tasks",listInStringFormat)
                apply()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        editor.apply {
            clear()
            apply()
        }
        if(taskList.size > 0){
            val listInStringFormat = convertListToString(taskList)
            editor.apply {
//                Toast.makeText(this@MainActivity, listInStringFormat, Toast.LENGTH_SHORT).show()
                putString("tasks",listInStringFormat)
                apply()
            }
        }

    }

    private fun convertListToString(list: ArrayList<Task>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    private fun convertJsonStringToList(str: String): ArrayList<Task>{
        val gson = Gson()
        val taskListType = object : TypeToken<ArrayList<Task>>() {}.type
        return gson.fromJson(str, taskListType)
    }

    fun addTask(){
        val taskTitle = etEnterTask.text.toString()
        val priority = taskSpinner.selectedItem.toString()
        val status = false
        if(taskTitle.trim()==""){
           showSnackBar("Please enter the task.")
            return
        }
        if(priority=="Select Priority"){
            showSnackBar("Please select Priority.")
            return
        }
        val task = Task(taskTitle, priority, status)
        taskList.add(task)
        rv.adapter!!.notifyItemInserted(taskList.size-1)
        setUpSpinner()
        etEnterTask.setText("")

    }

    private fun showSnackBar(str:String){
        Snackbar.make(findViewById(R.id.rootLayout), str, Snackbar.LENGTH_SHORT).show()
    }

    private fun setUpSpinner() {
        val options = arrayOf("Select Priority","High", "Medium", "Low")
        taskSpinner.adapter = ArrayAdapter<String>(this@MainActivity,android.R.layout.simple_list_item_1,options)
    }

    private fun initViews(){
        toolbar = findViewById(R.id.appbarlayout)
        taskSpinner = findViewById(R.id.spPriority)
        btnAddTask = findViewById(R.id.btnAddTask)
        rv = findViewById(R.id.rv)
        etEnterTask = findViewById(R.id.etTaskTitle)

    }

    override fun celebrate() {
        val party = Party(emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(30))
        val mp = MediaPlayer.create(this@MainActivity, R.raw.celebrate_tada)
        mp.start()
        val konfettiView= findViewById<KonfettiView>(R.id.konfettiView)
        konfettiView.start(party)

    }

}