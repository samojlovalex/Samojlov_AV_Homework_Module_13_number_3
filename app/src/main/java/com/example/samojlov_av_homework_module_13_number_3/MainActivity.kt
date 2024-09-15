package com.example.samojlov_av_homework_module_13_number_3

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.samojlov_av_homework_module_13_number_3.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@OptIn(ExperimentalStdlibApi::class)
class MainActivity : AppCompatActivity(), Removable {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbarMain: androidx.appcompat.widget.Toolbar
    private lateinit var toolbarPostMain: androidx.appcompat.widget.Toolbar
    private lateinit var postListSP: Spinner
    private lateinit var nameET: EditText
    private lateinit var surnameET: EditText
    private lateinit var ageET: EditText
    private lateinit var postSP: Spinner
    private lateinit var saveBT: Button
    private lateinit var listPersonLV: ListView

    private val listPosition = ListOfPositions()
    private var postInPerson: String = ""

    private var personList = mutableListOf<Person>()
    private var personListOut = mutableListOf<Person>()
    private var personListSort = listOf<Person>()
    private var filter: String? = null
    private var sort = false

    private var listAdapter: ArrayAdapter<Person>? = null

    private var postList = mutableListOf<String>()
    private var adapterPost: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }

    private fun init() {

        toolbarMain = binding.toolbarMain
        setSupportActionBar(toolbarMain)
        title = getString(R.string.toolbar_title)
        toolbarMain.subtitle = getString(R.string.toolbar_subtitle)

        toolbarPostMain = binding.toolbarPostMain
        toolbarPostMenuInit()

        postListSP = binding.postListSP
        nameET = binding.nameET
        surnameET = binding.surnameET
        ageET = binding.ageET
        postSP = binding.postSP
        saveBT = binding.saveBT
        listPersonLV = binding.listPersonLV

        listPersonInit()

        listPostInit()

        listAdapterInit()

        spinnerPersonEdit()
        spinnerPost()

        saveBT.setOnClickListener {
            if (nameET.text.isEmpty() || surnameET.text.isEmpty() || ageET.text.isEmpty() || postInPerson == getString(
                    R.string.post_first_item
                )
            ) return@setOnClickListener
            greatPerson()
            clearEditFields()
        }

        listPersonLV.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val note = listAdapter!!.getItem(position)
                val dialog = MyDialog()
                val args = Bundle()
                val type = typeOf<Person>().javaType
                val gson = Gson().toJson(note, type)
                args.putString("note", gson)
                dialog.arguments = args
                dialog.show(supportFragmentManager, "custom")
            }

    }

    private fun toolbarPostMenuInit() {
        toolbarPostMain.inflateMenu(R.menu.menu_list_person)
        toolbarPostMain.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.sortingMenu -> {
                    sort = true
                    listPersonInit()
                    listAdapterInit()
                    val snackbar =
                        Snackbar.make(
                            listPersonLV,
                            getString(R.string.sort_true_message), Snackbar.LENGTH_LONG
                        )
                    val snackbarView =
                        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    snackbarView.setMaxLines(10)
                    snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue))
                    snackbarView.setTextColor(ContextCompat.getColor(this, R.color.white))
                    snackbar.show()
                }

                R.id.notSortingMenu -> {
                    sort = false
                    listPersonInit()
                    listAdapterInit()
                }
            }
            true
        }
    }

    private fun listAdapterInit() {
        listAdapter = ListAdapter(this, personListOut)
        listPersonLV.adapter = listAdapter
        listAdapter!!.notifyDataSetChanged()
    }

    private fun listPersonInit() {
        personListSort = personList.toList().sortedBy { it.post }
        val argument = personList.toList().filter { it.post == filter }.toMutableList()
        personListOut = if (sort){
            if (argument.size == 0) personListSort.toMutableList()
            else personListSort.filter { it.post == filter }.toMutableList()
        } else {
            if (argument.size == 0) personList
            else personList.toList().filter { it.post == filter }.toMutableList()
        }
    }

    private fun listPostInit() {

        val postListInner = mutableListOf<String>()
        personList.forEach { postListInner.add(it.post) }
        val setList = postListInner.toSet()
        postList = setList.toMutableList()
        postList.add(0, getString(R.string.postList_first_item))
        if (!postList.contains(filter)) {
            postListSP.setSelection(0)
            filter = null
        }
    }

    private fun spinnerPost() {

        adapterPost = ArrayAdapter(this, R.layout.multiline_spinner_item_post, postList)
        adapterPost!!.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item_post)
        postListSP.adapter = adapterPost

        val itemSelectedListener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val item = parent?.getItemAtPosition(position) as String
                    filter = item
                    listPersonInit()
                    listAdapterInit()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        postListSP.onItemSelectedListener = itemSelectedListener

    }

    private fun spinnerPersonEdit() {
        val adapter = ArrayAdapter(this, R.layout.multiline_spinner_item, listPosition.list)
        adapter.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item)
        postSP.adapter = adapter

        val itemSelectedListener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val item = parent?.getItemAtPosition(position) as String
                    postInPerson = item
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        postSP.onItemSelectedListener = itemSelectedListener
    }

    private fun greatPerson() {
        val name = nameET.text.toString()
        val surname = surnameET.text.toString()
        val age = ageET.text.toString()

        val person = Person(name, surname, age, postInPerson)
        personList.add(person)
        listAdapter!!.notifyDataSetChanged()

        listPostInit()
        spinnerPost()

        Toast.makeText(
            this,
            getString(R.string.person_add_Toast, person.name, person.surname), Toast.LENGTH_LONG
        ).show()

    }

    private fun clearEditFields() {
        nameET.text.clear()
        surnameET.text.clear()
        ageET.text.clear()
        postInPerson = getString(R.string.poct_clear_text)
        postSP.setSelection(0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    @SuppressLint("SetTextI18n", "ShowToast")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exitMenu -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toast_exit),
                    Toast.LENGTH_LONG
                ).show()
                finishAffinity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun remove(note: String) {
        val type = typeOf<Person?>().javaType
        val noteRemove = Gson().fromJson<Person>(note, type)

        personList.remove(noteRemove)
        listAdapter!!.notifyDataSetChanged()
        listPostInit()
        spinnerPost()

        val snackbar =
            Snackbar.make(
                listPersonLV,
                getString(R.string.person_romove_snackbar_text, noteRemove), Snackbar.LENGTH_LONG
            )
        val snackbarView =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        snackbarView.setMaxLines(10)
        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue))
        snackbarView.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }
}