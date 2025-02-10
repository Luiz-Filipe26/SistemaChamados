package com.ticketsolutions.ticket_manager.core.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SQLCreator {
	
	private String tableName;
	
	public SQLCreator(String tableName) {
		this.tableName = tableName;
	}
	
    /**
     * Gera SQL para apagar por coluna
     */
	public String deleteBy(String column) {
		return "DELETE FROM " + this.tableName + " WHERE " + column + " = ?";
	}

    /**
     * Gera SQL para buscar tudo
     */
	public String findAll() {
		return "SELECT * FROM " + this.tableName;
	}

    /**
     * Gera SQL para buscar por coluna
     */
	public String findBy(String column) {
		return "SELECT * FROM " + this.tableName + " WHERE " + column + " = ?";
	}
	
    /**
     * Gera SQL para buscar por qualquer uma das colunas passadas (usando OR)
     */
    public String findByAny(String... columns) {
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException("At least one column must be provided.");
        }
      
        String whereClause = Stream.of(columns)
        		.map(col -> col + " = ?")
        		.collect(Collectors.joining(" OR "));

        return "SELECT * FROM " + this.tableName + " WHERE " + whereClause;
    }
	
	
    /**
     * Gera SQL para buscar por todas as colunas passadas (usando AND)
     */
    public String findByAll(String... columns) {
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException("At least one column must be provided.");
        }

        String whereClause = Stream.of(columns)
        		.map(col -> col + " = ?")
        		.collect(Collectors.joining(" AND "));

        return "SELECT * FROM " + this.tableName + " WHERE " + whereClause;
    }

    /**
     * Gera SQL para inserção
     */
    public String insert(SQLFields sqlFields) {
        String placeholders = sqlFields.columns().stream().map(v -> "?").collect(Collectors.joining(", "));
        return "INSERT INTO " + this.tableName + " (" + String.join(", ", sqlFields.columns()) + ") VALUES (" + placeholders + ")";
    }

    /**
     * Gera SQL para atualização (UPDATE)
     */
    public String update(SQLFields sqlFields) {
        String setClause = sqlFields.columns().stream().map(col -> col + " = ?").collect(Collectors.joining(", "));
        return "UPDATE " + this.tableName + " SET " + setClause + " WHERE id = ?";
    }

    /**
     * Processa um objeto para gerar as partes do SQL, os valores correspondentes e o HashMap de campos.
     */
    public SQLFields prepareFields(Object obj, Consumer<Map<String, Object>> fieldHandler) throws IllegalAccessException {
        Map<String, Object> fieldMap = convertToMap(obj);
        fieldHandler.accept(fieldMap);

        List<String> sqlParts = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (var entry : fieldMap.entrySet()) {
            if (entry.getValue() != null) {
                sqlParts.add(entry.getKey());
                values.add(entry.getValue());
            }
        }

        return new SQLFields(sqlParts, values, fieldMap);
    }

    /**
     * Converte um objeto genérico para um HashMap com os campos e valores correspondentes.
     */
    private Map<String, Object> convertToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> fieldMap = new HashMap<>();

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                fieldMap.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                throw new IllegalAccessException("Erro ao acessar o campo: " + field.getName());
            }
        }

        return fieldMap;
    }

    /**
     * Converte chaves do mapa de camelCase para snake_case.
     */
    public static void convertToSnakeCase(Map<String, Object> fieldMap) {
        var entries = new ArrayList<>(fieldMap.entrySet());
        fieldMap.clear();
        for (var entry : entries) {
            fieldMap.put(toSnakeCase(entry.getKey()), entry.getValue());
        }
    }

    /**
     * Converte um nome de atributo camelCase para snake_case.
     */
    private static String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

}