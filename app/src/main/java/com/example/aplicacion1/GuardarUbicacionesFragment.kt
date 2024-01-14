package com.example.aplicacion1

import LocationDbHelper
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText



class GuardarUbicacionesFragment : Fragment() {

    private lateinit var botonGuardarUbicacion: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_guardar_ubicaciones, container, false)

        val editTextLatitud = view?.findViewById<EditText>(R.id.latitud)
        val editTextLongitud = view?.findViewById<EditText>(R.id.longitud)
        val editTextDescripcion = view?.findViewById<EditText>(R.id.descripcion)


        botonGuardarUbicacion = view.findViewById(R.id.botonGuardarUbicacion)

        botonGuardarUbicacion.setOnClickListener {
            val latitud = editTextLatitud?.text.toString().toDoubleOrNull()
            val longitud = editTextLongitud?.text.toString().toDoubleOrNull()
            val descripcion = editTextDescripcion?.text.toString()
            editTextLatitud?.setText("")
            editTextLongitud?.setText("")
            editTextDescripcion?.setText("")
            val dbHelper = LocationDbHelper(requireContext())
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(LocationContract.LocationEntry.COLUMN_LATITUD, latitud)
                put(LocationContract.LocationEntry.COLUMN_LONGITUD, longitud)
                put(LocationContract.LocationEntry.COLUMN_DESCRIPCION, descripcion)
            }

            val newRowId = db?.insert(LocationContract.LocationEntry.TABLE_NAME, null, values)
        }

        return view
    }
}
