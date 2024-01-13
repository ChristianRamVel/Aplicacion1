package com.example.aplicacion1

import LocationDbHelper
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VerUbicacionesFragment : Fragment() {

    private var botonAñadirUbicacion: Button? = null
    private var botonBorrarUbicacion: Button? = null
    private var botonVerUbicacion: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ver_ubicaciones, container, false)

        val listviewUbicaciones = view.findViewById<ListView>(R.id.listviewUbicaciones)
        val ubicaciones = obtenerUbicacionesDesdeBaseDeDatos()
        val ubicacionesAdapter = UbicacionesAdapter(requireContext(), ubicaciones)

        botonAñadirUbicacion = view.findViewById(R.id.botonAñadirUbicacion)
        botonBorrarUbicacion = view.findViewById(R.id.botonBorrarUbicacion)
        botonVerUbicacion = view.findViewById(R.id.botonVerUbicacion)

        botonAñadirUbicacion?.setOnClickListener {
            openGuardarUbicacionesFragment()
        }

        botonBorrarUbicacion?.setOnClickListener {
            val dbHelper = LocationDbHelper(requireContext())
            val db = dbHelper.writableDatabase
            db.delete(LocationContract.LocationEntry.TABLE_NAME, null, null)
            val ubicaciones = obtenerUbicacionesDesdeBaseDeDatos()
            val ubicacionesAdapter = UbicacionesAdapter(requireContext(), ubicaciones)
            listviewUbicaciones.adapter = ubicacionesAdapter
        }

        botonVerUbicacion?.setOnClickListener {
            val ubicaciones = obtenerUbicacionesDesdeBaseDeDatos()
            if (ubicaciones.isNotEmpty()) {
                val ubicacion = ubicaciones[0]

                val intent = Uri.parse("geo:${ubicacion.latitud},${ubicacion.longitud}")
                val intentMapa = Intent(Intent.ACTION_VIEW, intent)
                intentMapa.setPackage("com.example.mapaapp")
                startActivity(intentMapa)
            }
        }

        listviewUbicaciones.setOnItemClickListener { _, _, position, _ ->
            val ubicacionSeleccionada = ubicacionesAdapter.getItem(position) as Ubicacion
            mostrarUbicacionEnMapa(ubicacionSeleccionada)
        }

        return view
    }

    private fun mostrarUbicacionEnMapa(ubicacion: Ubicacion) {
        val intent = requireActivity().packageManager.getLaunchIntentForPackage("com.example.mapaapp")

        if (intent != null) {
            intent.action = "com.example.mapaapp.MOSTRAR_UBICACION"
            intent.putExtra("latitud", ubicacion.latitud)
            intent.putExtra("longitud", ubicacion.longitud)
            startActivity(intent)
        } else {
            // Manejar el caso en el que la aplicación no esté instalada
            Log.e("VerUbicacionesFragment", "La aplicación de mapas no está instalada.")
        }
    }
    //inflar listview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ubicaciones = obtenerUbicacionesDesdeBaseDeDatos()
        val ubicacionesAdapter = UbicacionesAdapter(requireContext(), ubicaciones)
        val listviewUbicaciones = view.findViewById<ListView>(R.id.listviewUbicaciones)
        listviewUbicaciones.adapter = ubicacionesAdapter
    }

    class UbicacionesAdapter(context: Context, ubicaciones: List<Ubicacion>) : BaseAdapter() {
        private val mContext: Context = context
        private val mUbicaciones: List<Ubicacion> = ubicaciones

        override fun getCount(): Int {
            return mUbicaciones.size
        }

        override fun getItem(position: Int): Any {
            return mUbicaciones[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val ubicacion = getItem(position) as Ubicacion
            val textView = TextView(mContext)
            textView.text = "Latitud: ${ubicacion.latitud}, Longitud: ${ubicacion.longitud}"
            return textView
        }

        companion object {
            fun getItem(position: Int): Any {
                return position
            }
        }
    }

    private fun openGuardarUbicacionesFragment() {
        // Crear una instancia del fragmento GuardarUbicacionesFragment
        val guardarUbicacionesFragment = GuardarUbicacionesFragment()

        // Obtener el FragmentManager
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager

        // Iniciar una transacción de fragmento
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Reemplazar el fragmento actual con el nuevo fragmento
        transaction.replace(R.id.fragment_container, guardarUbicacionesFragment)

        // Añadir la transacción a la pila de retroceso
        transaction.addToBackStack(null)

        // Confirmar la transacción
        transaction.commit()
    }

    //obtener ubicaciones desde la base de datos
    private fun obtenerUbicacionesDesdeBaseDeDatos(): List<Ubicacion> {
        val ubicaciones = mutableListOf<Ubicacion>()
        val dbHelper = LocationDbHelper(requireContext())
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            LocationContract.LocationEntry.COLUMN_LATITUD,
            LocationContract.LocationEntry.COLUMN_LONGITUD
        )
        val cursor = db.query(
            LocationContract.LocationEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        with(cursor) {
            while (moveToNext()) {
                val latitud = getDouble(getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LATITUD))
                val longitud = getDouble(getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LONGITUD))
                ubicaciones.add(Ubicacion(latitud, longitud))
            }
        }
        return ubicaciones
    }
}