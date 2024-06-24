package com.aluracursos.desafio.principal;
import com.aluracursos.desafio.model.Datos;
import com.aluracursos.desafio.model.DatosLibros;
import com.aluracursos.desafio.service.ConsumoAPI;
import com.aluracursos.desafio.service.ConvierteDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private final String URL = "https://gutendex.com/books/";

    public void muestraInicio() {
        System.out.println("* JSON: ");
        var json = consumoAPI.obtenerDatos(URL);
        System.out.println(json);

        System.out.println("\n* Datos: ");
        var datos = conversor.obtenerDatos(json, Datos.class);
        datos.libros().stream().limit(5).forEach(System.out::println);
        //System.out.println(datos);

        // Top 10 libros más descargados
        System.out.println("\n\n* Top 10 libros más descargados: ");
        datos.libros().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .map(l -> l.titulo().toUpperCase())
                .forEach(System.out::println);


        // Busca libros por fragmento del nombre
        System.out.println("\n¿Qué libro deseas buscar? : ");
        var busquedaLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL + "?search=" + busquedaLibro.toLowerCase().replace(" ", "%20"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.libros().stream()
                .filter(l -> l.titulo().toUpperCase().contains(busquedaLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()) {
            System.out.println("\n* Libro encontrado: " + libroBuscado.get());
        } else System.out.println("**** Libro no en contrado ****");

        // Trabajando con estadísticas
        DoubleSummaryStatistics est = datos.libros().stream()
                .filter(d -> d.numeroDeDescargas() > 0)
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas));
        System.out.println("\nCantidad media de descargas: " + est.getAverage());
        System.out.println("Cantidad máxima de descargas: " + est.getMax());
        System.out.println("Cantidad mínima de descargas " + est.getMin());
        System.out.println("Cantidad de registros ecaluados para calcular las estradisticas: " + est.getCount());

    }
}
