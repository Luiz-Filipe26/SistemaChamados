package com.ticketsolutions.ticket_manager.core.utils;

import java.util.List;
import java.util.Map;

public record SQLFields(List<String> columns, List<Object> values, Map<String, Object> fieldMap) {}
