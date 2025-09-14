package com.mastermindcms.common.util;

import com.mastermindcms.modules.beans.GroupFilter;
import com.mastermindcms.modules.beans.PageNavigation;
import com.mastermindcms.modules.beans.SearchRequest;
import com.mastermindcms.modules.enums.FiltersStrategy;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MastermindUtils {

    private final static Logger logger = LoggerFactory.getLogger(MastermindUtils.class);

    private static final CsvMapper csvMapper = new CsvMapper();

    private MastermindUtils(){
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String getItemId(JSONObject json) {
        JSONObject itemId = null;
        String strId = null;
        if(isJSONValid(json.get("_id").toString())){
            itemId = new JSONObject(json.get("_id").toString());
            strId = itemId.get("$oid") != null ? itemId.get("$oid").toString() : itemId.toString();
        } else {
            strId = json.get("_id").toString();
        }

        return strId;
    }

    public static <T> T getBeanValue(Object instance, String placeholderValue, int groupIndex){
        Matcher multiple = Pattern.compile("(.+?)\\.(.+?)").matcher(placeholderValue);
        Matcher single = Pattern.compile("(.+?)").matcher(placeholderValue);
        Matcher arrayIndex = Pattern.compile("\\w+\\[\\d\\]").matcher(placeholderValue);
        T result = null;
        boolean isMap = (instance instanceof Map);
        boolean isSet = (instance instanceof Set);
        boolean isList = (instance instanceof List);
        boolean isCollection = (isMap || isList || isSet);
        boolean isSizeMethod = placeholderValue.equals("size");
        if (multiple.matches()) {
            if(isMap) {
                result = invokeGetterFromMap(instance, multiple.group(groupIndex));
            } else if(isSet) {
                result = invokeGetterFromSet(instance, multiple.group(groupIndex));
            } else {
                result = invokeField(instance,multiple.group(groupIndex));
            }

            if(multiple.groupCount() > 0) {
                result = getBeanValue(result, multiple.group(groupIndex + 1), groupIndex);
            }
        } else if(single.matches()) {
            if(arrayIndex.matches()) {
                T[] array;
                String key = placeholderValue.substring(0,placeholderValue.indexOf("["));
                String idxStr = placeholderValue.substring(placeholderValue.indexOf("[")+1, placeholderValue.indexOf("]"));
                if(isMap) {
                    array = invokeGetterFromMap(instance,key);
                } else {
                    array = invokeField(instance,key);
                }
                int idx = Integer.parseInt(idxStr);
                result = Objects.nonNull(array) ? (T) array[idx] : null;
            } else {
                if(isCollection && isSizeMethod) {
                    result = invokeMethod(instance,placeholderValue);
                } else if(isMap) {
                    result = invokeGetterFromMap(instance,placeholderValue);
                } else if(isSet) {
                    result = invokeGetterFromSet(instance, placeholderValue);
                } else {
                    result = invokeField(instance,placeholderValue);
                }
            }
        }
        return result;
    }

    public static boolean isMethodExists(Object instance, String methodName) {
        boolean methodExists = false;
        try {
            instance.getClass().getDeclaredMethod(methodName);
            methodExists = true;
        } catch (NoSuchMethodError | NoSuchMethodException e) {
            // ignore
        }
        return methodExists;
    }

    public static boolean isFieldExists(Object instance, String fieldName) {
        return Arrays.stream(instance.getClass().getDeclaredFields())
                .anyMatch(f -> {
                    f.setAccessible(true);
                    return f.getName().equals(fieldName);
                });
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object instance, String methodName) {
        T result = null;
        if(Objects.nonNull(instance) && !methodName.contains("()")) {
            try {
                Method method = instance.getClass().getMethod(methodName);
                method.setAccessible(true);
                result = (T)method.invoke(instance);
            } catch (InvocationTargetException e) {
                String message = e.getTargetException().getMessage();
                if (message == null) {
                    message = e.getCause().getMessage();
                }

                logger.error("Method " + methodName + " from " + getRealClassName(instance) + " (invokeMethod): " + message);
                if (message == null) {
                    e.getCause().printStackTrace();
                }
            } catch (NoSuchMethodException | IllegalAccessException e) {
                logger.error(e.getClass().getName() + " for method "+ methodName +" from "+ getRealClassName(instance)  +" (invokeMethod)");
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object instance, String methodName, Object[] args, Class<?>[] types) {
        T result = null;
        boolean isMap = args.length > 0 ? (args[0] instanceof Map) : false;
        List<Object> values;
        if(Objects.nonNull(instance) && !methodName.contains("()")) {
            if (isMap) {
                Map<String, Object> arguments = args.length > 0 ? (LinkedHashMap<String, Object>) args[0] : new HashMap<>();
                values = new ArrayList<>(arguments.values());
            } else {
                values = Arrays.asList(args);
            }

            try {
                Method method = instance.getClass().getMethod(methodName, types);
                method.setAccessible(true);
                result = (T) method.invoke(instance, values.toArray());
            } catch (InvocationTargetException e) {
                String message = e.getTargetException().getMessage();
                if (message == null) {
                    message = e.getCause().getMessage();
                }

                logger.error("Method " + methodName + " from " + getRealClassName(instance) + " (invokeMethod with args): " + message);
                if (message == null) {
                    e.getCause().printStackTrace();
                }
            } catch (NoSuchMethodException | IllegalAccessException e) {
                logger.error(e.getClass().getName() + " for method "+ methodName +" from "+ getRealClassName(instance)  +" (invokeMethod with args)");
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeGetterFromMap(Object instance, String keyValue) {
        String regexNum = "\\[([^)]+)\\]";
        Matcher matcher = Pattern.compile(regexNum).matcher(keyValue);
        if (matcher.find()) {
            int idx = Integer.parseInt(matcher.group(1));
            String key = keyValue.replaceAll(regexNum, "");
            Collection<Object> collection = (Collection<Object>) ((Map<String, Object>) instance).get(key);
            if(Objects.nonNull(collection) && !collection.isEmpty()) {
                List<Object> list = new ArrayList<>(collection);
                return (T) list.get(idx);
            }
        }
        return (T) ((Map<String,Object>)instance).get(keyValue);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeGetterFromSet(Object instance, String keyValue) {
        Set<T> setOfObjects = ((HashSet<T>)instance).stream()
            .map(o -> {
                Map<String,Object> map = convertBeanToMap(o);
                return (T)map.get(keyValue);
            }).collect(Collectors.toSet());

        return (T)setOfObjects;
    }

    public static <T> void invokeSetter(Object obj, String propertyName, Object variableValue) {
        PropertyDescriptor pd;
        if(Objects.nonNull(obj)) {
            try {
                pd = new PropertyDescriptor(propertyName, obj.getClass());
                Method setter = pd.getWriteMethod();
                try {
                    setter.invoke(obj, variableValue);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    logger.error("Setter "+ propertyName +" from "+ getRealClassName(obj) +" (invokeSetter): " + e.toString());
                }
            } catch (IntrospectionException e) {
                logger.error("Setter "+ propertyName +" from "+ getRealClassName(obj) +" (invokeSetter): " + e.toString());
            }
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> T invokeGetter(Object obj, String variableName) {
        T f = null;
        if(Objects.nonNull(obj)) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(variableName, obj.getClass());
                Method getter = pd.getReadMethod();
                f = (T)getter.invoke(obj);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
                logger.error("Getter "+ variableName +" from "+ getRealClassName(obj) +"(invokeGetter): " + e.toString());
            }
        }
        return f;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeField(Object instance, String fieldName) {
        T result = null;
        if(Objects.nonNull(instance)) {
            try {
                Field field = instance.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                result = (T)field.get(instance);
            } catch (Exception e) {
                logger.error("Field "+ fieldName +" from "+ getRealClassName(instance) +" (invokeField): " + e.toString());
            }
        }
        return result;
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    // then use Spring BeanUtils to copy and ignore null
    public static void сopyProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    @SuppressWarnings("unchecked")
    public static Class<?>[] getTypesFromList(Object[] args){
        List<Object> list = Arrays.asList(args);
        List<Class<?>> types = new ArrayList<>();
        Map<String,Object> arguments = (LinkedHashMap<String,Object>)list.get(0);
        arguments.forEach((k,v) -> {
            try {
                Class<?> cls = Objects.nonNull(v) ?
                        Class.forName(v.getClass().getName()) :
                        null;

                types.add(cls);
            } catch (ClassNotFoundException e) {
                logger.error("Method (getTypesFromList): " + e.toString());
            }
        });
        Class<?>[] typesArray = new Class<?>[types.size()];
        return types.toArray(typesArray);
    }

    public static int whiteSpaceCount(String s)
    {
        int i,c;
        for(i=0,c=0;i<s.length();i++){
            char ch=s.charAt(i);
            if(ch==' ') {
                c++;
            }
        }
        return c;
    }

    public static <T> List<T> readCSV(Class<T> clazz, InputStream stream) throws IOException {
        CsvSchema schema = csvMapper.schemaFor(clazz)
                .withHeader()
                .withColumnReordering(true);
        ObjectReader reader = csvMapper.readerFor(clazz).with(schema);
        return reader.<T>readValues(stream).readAll();
    }

    public static <T> void writeCSV(T[] data, File file, Class<T> clazz) throws IOException {
        CsvSchema schema = csvMapper.schemaFor(clazz)
                .withHeader()
                .withColumnSeparator('|')
                .withColumnReordering(true);
        csvMapper.findAndRegisterModules();
        csvMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        csvMapper.writerFor(((T[]) Array.newInstance(clazz, data.length)).getClass())
                .with(schema).writeValue(file, data);
    }

    public static String getRemoteAddress(String url) {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        if (attribs != null) {
            HttpServletRequest request = ((ServletRequestAttributes) attribs).getRequest();
            final String scheme = request.getScheme();
            final String host = request.getServerName();
            final String contextPath  = request.getContextPath();
            int port = request.getServerPort();
            final String uri = scheme + "://" + host + ":" + port + contextPath + url;
            return uri;
        }
        return null;
    }

    public static String getSessionId(String cookie){
        return new String(Base64.getDecoder().decode(cookie.split(";")[0].split("=")[1]));
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getRepositoryEntityClass(Class<T> repository, String basePackage) {
        Type[] interfaces = repository.getInterfaces();

        for (Type t : interfaces) {
            if (t instanceof Class<?>) {
                Class<?> clazz = (Class<?>) t;

                if (clazz.getPackage().getName().startsWith(basePackage)) {
                    // Repositories should implement only ONE interface from application packages
                    Type genericInterface = clazz.getGenericInterfaces()[0];
                    return (Class<T>) ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
                }
            }
        }

        return null;
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toList());
        result.addAll(filteredFields);
        return result;
    }

    public static String getMethodDefinitionName(String methodDefinitionWithBrackets){
        String regex = "\\w+";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(methodDefinitionWithBrackets);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }

    public static List<Object> splitArgsFromMethodDefinition(String methodDefinition){
        String regex = "\\((.*?)(?!.*\\))[^)]*[^(]*$";
        Pattern p = Pattern.compile(regex);   // the pattern to search for
        Matcher m = p.matcher(methodDefinition);

        if (m.find()) {
            String groupDef = m.group(0);
            String argsDef = groupDef.substring(1, groupDef.length()-1);
            List<Object> args = Arrays.asList(argsDef.split(";"));

            LinkedHashMap<String, Object> collect = args
                    .stream()
                    .collect(LinkedHashMap::new,
                            (map, streamValue) -> map.put(String.valueOf(map.size()), streamValue),
                            (map, map2) -> {
                    });

            List<Object> result = new ArrayList<>();
            result.add(collect);

            return result;
        }
        return Collections.emptyList();
    }

    public static Map<String, Object> convertBeanToMap(Object bean) {
        Map<String, Object> objectAsMap = new HashMap<>();
        BeanInfo info;
        if(Objects.nonNull(bean)) {
            try {
                info = Introspector.getBeanInfo(bean.getClass());
                for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                    Method reader = pd.getReadMethod();
                    if (reader != null && !pd.getName().equalsIgnoreCase("class")) {
                        objectAsMap.put(pd.getName(), reader.invoke(bean));
                    }
                }
            } catch (IntrospectionException | IllegalAccessException | InvocationTargetException | NullPointerException e) {
                logger.error("Bean " + getRealClassName(bean) + " (convertBeanToMap): " + e.toString());
            }
        } else {
            logger.error("Bean (convertBeanToMap) : Bean is not defined!");
        }
        return objectAsMap;
    }

    public static Map<String,Object> flatCollections(Map<String,Object> srcMap, String keyPrefixToFlat){
        List<List<Object>> combined =  srcMap.entrySet().stream()
                .filter(i -> i.getKey().contains(keyPrefixToFlat))
                .map(i -> (List<Object>) new ArrayList<Object>((Collection<?>) i.getValue()))
                .flatMap(Stream::of)
                .collect(Collectors.toList());
        List<Object> result = combined.stream().flatMap(List::stream).collect(Collectors.toList());
        srcMap.entrySet().removeIf(i -> i.getKey().contains(keyPrefixToFlat));
        srcMap.put(keyPrefixToFlat,result);
        return srcMap;
    }

    public static String clearContactInfoInText(String text) {
        return text
                .replaceAll("(?i)\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))","")
                .replaceAll("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])","")
                .replaceAll("(?:(?:\\+?([1-9]|[0-9][0-9]|[0-9][0-9][0-9])\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([0-9][1-9]|[0-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?","");

    }

    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if(Objects.nonNull(requestAttributes) && requestAttributes instanceof ServletRequestAttributes){
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }

        return null;
    }

    public static String inputStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    public static boolean isStringContainsAnyItemFromList(String str, List<String> list) {
        return Objects.nonNull(list) && list.stream().anyMatch(str::contains);
    }

    public static boolean isStringContainsAllItemsFromList(String str, List<String> list, boolean ignoreCase) {
        if(ignoreCase){
            return Objects.nonNull(list) && list.stream().allMatch(s -> StringUtils.containsIgnoreCase(str,s));
        } else {
            return Objects.nonNull(list) && list.stream().allMatch(str::contains);
        }
    }

    public static <T> Collector<T, ?, List<T>> lastElements(long n) {
        return Collector.<T, Deque<T>, List<T>>of(ArrayDeque::new, (acc, t) -> {
            if(acc.size() == n)
                acc.pollFirst();
            acc.add(t);
        }, (acc1, acc2) -> {
            while(acc2.size() < n && !acc1.isEmpty()) {
                acc2.addFirst(acc1.pollLast());
            }
            return acc2;
        }, ArrayList::new);
    }

    public static String getRealClassName(Object obj){
        Class<?>[] interfaces = obj.getClass().getInterfaces();
        Class<?> clazz = interfaces.length > 0 ? interfaces[0] : obj.getClass();
        return clazz.getName();
    }

    public static Collection<Object> remapKeysWithPrefixRecursively(Collection<?> listOfItems, String keyPrefix, String keyForNestedArray) {
        return listOfItems.stream()
                .map(MastermindUtils::convertBeanToMap)
                .map(map -> map.entrySet().stream().collect(HashMap::new, (m, v) -> m.put(keyPrefix + v.getKey(), v.getValue()), HashMap::putAll))
                .peek(item -> {
                    String remappedKey = keyPrefix+keyForNestedArray;
                    if(item.containsKey(remappedKey)){
                        Collection<?> items = (Collection<?>) item.get(remappedKey);
                        Collection<Object> remappedItems = remapKeysWithPrefixRecursively(items,keyPrefix,keyForNestedArray);
                        item.put(remappedKey,remappedItems);
                    }
                }).collect(Collectors.toList());

    }

    public static String urlEncode(String src) {
        return UriUtils.encodePath(src, StandardCharsets.UTF_8.toString());
    }

    public static String urlDecode(String src) {
        return UriUtils.decode(src, StandardCharsets.UTF_8.toString());
    }

    public static boolean isFileExists(String uri){
        File f = new File(URI.create(uri).getPath());
        return f.isFile() && !f.isDirectory();
    }

    public static String getQueryParameterFromUrl(String url, String param, Integer idx){
        int index = Objects.nonNull(idx) ? idx : 0;
        MultiValueMap<String, String> parameters =
                UriComponentsBuilder.fromHttpUrl(url).build().getQueryParams();
        return Optional.ofNullable(parameters.get(param)).map(p -> p.get(index)).orElse(null);
    }

    public static String generateHMAC(String algorithm, String data, String key){
        return new HmacUtils(algorithm, key).hmacHex(data);
    }

    @SafeVarargs
    public static <T> List<T> mergeCollections(Collection<T>... collections){
        return Stream.of(collections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    };

    public static File getFileFromResources(String path) throws URISyntaxException {
        File file = null;
        try {
            if (path.startsWith("classpath:")) {
                String resourceLocation = path.substring("classpath:".length());
                file = new ClassPathResource(resourceLocation).getFile();
            } else {
                file = ResourceUtils.getFile(path);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        return file;
    }

    public static List<File> getResourceFiles(String path) throws IOException, URISyntaxException {
        List<File> files = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream in = classLoader.getResourceAsStream(path) != null ?
                classLoader.getResourceAsStream(path) :
                MastermindUtils.class.getResourceAsStream(path);
        if(Objects.nonNull(in)){
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String resource;

                while ((resource = br.readLine()) != null) {
                    File file = getFileFromResources(resource);
                    files.add(file);
                }
            }
        }
        return files;
    }

    public static <T> Deque<Map<String, Object>> getPagination(JSONObject json, Page<T> pages) {
        int page = 0;
        Integer visiblePages = null;
        Deque<Map<String, Object>> result = new ArrayDeque<>();

        Map<String, Object> prevItem = new HashMap<>();
        Map<String, Object> moreItem = new HashMap<>();
        Map<String, Object> totalPagesItem = new HashMap<>();

        Map<String, Object> firstItem = new HashMap<>();
        firstItem.put("number", 1);
        firstItem.put("value", 1);
        firstItem.put("numberOfElements", pages.getTotalElements());

        if (json.has("visiblePages")) {
            visiblePages = Math.min(pages.getTotalPages(), json.getInt("visiblePages"));
        }

        if (json.has("query")) {
            JSONObject queryJson = json.getJSONObject("query");
            if (queryJson.has("page")) {
                page = queryJson.getInt("page") != 1 ? queryJson.getInt("page") - 1 : 0;
            }
        }

        if (Objects.nonNull(visiblePages)) {
            int finish = (pages.getTotalPages() - page) < visiblePages ? pages.getTotalPages() : page + visiblePages;
            for (int i = page; i < finish; i++) {
                Map<String, Object> mapItem = new HashMap<>();
                mapItem.put("number", i + 1);
                mapItem.put("value", i + 1);
                mapItem.put("numberOfElements", pages.getTotalElements());
                result.add(mapItem);
            }
            boolean displayMore = (pages.getTotalPages() - page) > visiblePages;
            boolean displayPrev = page >= 2;

            if (displayMore) {
                moreItem.put("number", "...");
                moreItem.put("value", page + visiblePages + 1);
                moreItem.put("numberOfElements", pages.getTotalElements());

                totalPagesItem.put("number", pages.getTotalPages());
                totalPagesItem.put("value", pages.getTotalPages());
                totalPagesItem.put("numberOfElements", pages.getTotalElements());

                result.addLast(moreItem);
                result.addLast(totalPagesItem);
            }

            if(displayPrev){
                prevItem.put("number", "...");
                prevItem.put("value", page);
                prevItem.put("numberOfElements", pages.getTotalElements());

                result.addFirst(prevItem);
                result.addFirst(firstItem);
            }
        }

        return result;
    }

    public static <T> Deque<PageNavigation> getPagination(int page, int visiblePages, Page<T> pages) {
        Deque<PageNavigation> result = new ArrayDeque<>();

        PageNavigation prevItem = new PageNavigation();
        PageNavigation moreItem = new PageNavigation();
        PageNavigation totalPagesItem = new PageNavigation();

        PageNavigation firstItem = new PageNavigation();
        firstItem.setNumber(1);
        firstItem.setValue(1);
        firstItem.setNumberOfElements(pages.getTotalElements());

        visiblePages = Math.min(pages.getTotalPages(), visiblePages);
        page = page != 1 ? page - 1 : 0;

        if (visiblePages > 0) {
            int finish = (pages.getTotalPages() - page) < visiblePages ? pages.getTotalPages() : page + visiblePages;
            for (int i = page; i < finish; i++) {
                PageNavigation pageItem = new PageNavigation();
                pageItem.setNumber(i + 1);
                pageItem.setValue(i + 1);
                pageItem.setNumberOfElements(pages.getTotalElements());
                result.add(pageItem);
            }
            boolean displayMore = (pages.getTotalPages() - page) > visiblePages;
            boolean displayPrev = page >= 2;

            if (displayMore) {
                moreItem.setNumber("...");
                moreItem.setValue(page + visiblePages + 1);
                moreItem.setNumberOfElements(pages.getTotalElements());

                totalPagesItem.setNumber(pages.getTotalPages());
                totalPagesItem.setValue(pages.getTotalPages());
                totalPagesItem.setNumberOfElements(pages.getTotalElements());

                result.addLast(moreItem);
                result.addLast(totalPagesItem);
            }

            if(displayPrev){
                prevItem.setNumber("...");
                prevItem.setValue(page);
                prevItem.setNumberOfElements(pages.getTotalElements());

                result.addFirst(prevItem);
                result.addFirst(firstItem);
            }
        }

        return result;
    }

    public static String wrapQueryToMatchOperation(SearchRequest request) {
        JSONObject queryJson;
        JSONObject matchOperation = new JSONObject();
        if(Objects.nonNull(request.getQueryAsString())){
            queryJson = new JSONObject(request.getQueryAsString());
        } else {
            queryJson = new JSONObject(request.getQuery());
        }
        matchOperation.put("$match",queryJson);
        return matchOperation.toString();
    }

    public static SearchRequest transformQueryWithRegexp(SearchRequest request){
        Map<String,Object> requestInitQuery = request.getQuery();
        List<String> ignoreRegexWrap = request.getIgnoreRegexWrap();
        Map<String,Object> regexpQuery = requestInitQuery
                .entrySet()
                .stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey,(e)->{
                    String key = e.getKey().contains(".") ? e.getKey().split("\\.")[0] : e.getKey();
                    boolean isRegexWrap = !isStringContainsAnyItemFromList(key,ignoreRegexWrap);
                    if(isRegexWrap) {
                        String[] values = e.getValue().toString().split("\\|");
                        String val = Arrays.stream(values)
                                .map(v -> "(^|\\|)"+ Pattern.quote(v)+"($|\\|)")
                                .collect(Collectors.joining("|"));
                        Map<String,Object> nested = new HashMap<>();
                        nested.put("$regex", val);
                        return nested;
                    } else {
                        return e.getValue();
                    }
                }));
        JSONObject queryJson = new JSONObject(regexpQuery);
        request.setQuery(queryJson.toMap());
        request.setQueryAsString(queryJson.toString());
        return request;
    }

    public static SearchRequest transformFiltersToQuery(SearchRequest request) {
        FiltersStrategy strategy = Optional.ofNullable(request.getFilteringStrategy())
                .orElse(FiltersStrategy.EXCLUDE);
        if(Objects.nonNull(request.getIgnoreRegexWrap()) && !request.getIgnoreRegexWrap().isEmpty()
                && strategy == FiltersStrategy.INCLUDE){
            transformQueryWithRegexp(request);
        }
        Map<String,Object> requestInitQuery = request.getQuery();
        String parsedFilters = String.join(",","filters","rangeFilters","numericFilters","publishedForSalePerm","publishedForSaleRemains");
        List<String> keys = new ArrayList<>(request.getQuery().keySet());
        if(MastermindUtils.isStringContainsAnyItemFromList(parsedFilters,keys)){
            JSONArray arrayFilters = new JSONArray();
            String dynamicRange,numericValue;
            String publishedForSalePerm;
            String publishedForSaleRemains;
            JSONObject queryJson = new JSONObject(requestInitQuery);

            if(queryJson.has("$and")) {
                arrayFilters.putAll(queryJson.getJSONArray("$and"));
            }

            if(strategy == FiltersStrategy.EXCLUDE) {
                if (queryJson.has("filters")) {
                    JSONArray otherFilters = queryJson.get("filters") instanceof JSONArray ?
                            queryJson.getJSONArray("filters") : new JSONArray(queryJson.getString("filters").split("\\|"));
                    queryJson.remove("filters");
                    JSONObject filters = new JSONObject();
                    JSONObject allJson = new JSONObject();
                    allJson.put("$all", otherFilters);
                    filters.put("filters", allJson);
                    arrayFilters.put(filters);
                    if (!arrayFilters.isEmpty()) {
                        queryJson.put("$and", arrayFilters);
                    }
                }
            }

            if(queryJson.has("rangeFilters")){
                dynamicRange = queryJson.getString("rangeFilters");
                queryJson.remove("rangeFilters");
                String[] filterArr = dynamicRange.split("\\|");
                List<GroupFilter> filters = Arrays.stream(filterArr).map(filter -> {
                    String[] f = filter.split("-");
                    String attributeId = f.length > 1 ? f[0] : null;
                    Double minValue = f.length >= 2 ? Double.parseDouble(f[1]) : null;
                    Double maxValue = f.length >= 3 ? Double.parseDouble(f[2]) : null;
                    return GroupFilter.builder()
                            .id(attributeId)
                            .min(minValue)
                            .max(maxValue)
                            .value(minValue)
                            .build();
                }).collect(Collectors.toList());

                Map<String,List<GroupFilter>> filtersMap = new HashMap<>();
                for (GroupFilter groupFilter : filters) {
                    if(filtersMap.containsKey(groupFilter.getId())) {
                        filtersMap.get(groupFilter.getId()).add(groupFilter);
                    } else {
                        List<GroupFilter> values = new ArrayList<>();
                        values.add(groupFilter);
                        filtersMap.put(groupFilter.getId(),values);
                    }
                }

                for (Map.Entry<String,List<GroupFilter>> entry : filtersMap.entrySet()) {
                    JSONObject rangeFilter = new JSONObject();
                    JSONArray orJson = entry.getValue().stream().map(filter -> {
                        JSONObject filterJson = new JSONObject();
                        JSONObject gteJson = new JSONObject();
                        JSONObject lteJson = new JSONObject();

                        if(Objects.isNull(filter.getMin())){
                            gteJson.put("$gte",0);
                            lteJson.put("$lte",0);
                            filterJson.put("rangeFilters.min", lteJson);
                            filterJson.put("rangeFilters.max", gteJson);
                        }

                        if(Objects.nonNull(filter.getMin()) && Objects.isNull(filter.getMax())){
                            gteJson.put("$gte",filter.getMin());
                            lteJson.put("$lte",filter.getMin());
                            filterJson.put("rangeFilters.min", lteJson);
                            filterJson.put("rangeFilters.max", gteJson);
                        }

                        if(Objects.nonNull(filter.getMin()) && Objects.nonNull(filter.getMax())){
                            gteJson.put("$gte",filter.getMin());
                            lteJson.put("$lte",filter.getMax());
                            filterJson.put("rangeFilters.min", gteJson);
                            filterJson.put("rangeFilters.max", lteJson);
                        }
                        return filterJson;
                    }).collect(Collector.of(
                            JSONArray::new, //init accumulator
                            JSONArray::put, //processing each element
                            JSONArray::put  //confluence 2 accumulators in parallel execution
                    ));
                    rangeFilter.put("rangeFilters.attributeId",entry.getKey());
                    rangeFilter.put("$or",orJson);
                    arrayFilters.put(rangeFilter);
                }
                if(!arrayFilters.isEmpty()) {
                    queryJson.put("$and",arrayFilters);
                }
            }

            if(queryJson.has("numericFilters")){
                numericValue = queryJson.getString("numericFilters");
                queryJson.remove("numericFilters");
                String[] filterArr = numericValue.split("\\|");
                List<GroupFilter> filters = Arrays.stream(filterArr).map(filter -> {
                    String[] f = filter.split("-");
                    String attributeId = f.length > 1 ? f[0] : null;
                    Double minValue = f.length >= 2 ? Double.parseDouble(f[1]) : null;
                    Double maxValue = f.length >= 3 ? Double.parseDouble(f[2]) : null;
                    return GroupFilter.builder()
                            .id(attributeId)
                            .min(minValue)
                            .max(maxValue)
                            .value(minValue)
                            .build();
                }).collect(Collectors.toList());

                Map<String,List<GroupFilter>> filtersMap = new HashMap<>();
                for (GroupFilter groupFilter : filters) {
                    if(filtersMap.containsKey(groupFilter.getId())) {
                        filtersMap.get(groupFilter.getId()).add(groupFilter);
                    } else {
                        List<GroupFilter> values = new ArrayList<>();
                        values.add(groupFilter);
                        filtersMap.put(groupFilter.getId(),values);
                    }
                }

                for (Map.Entry<String,List<GroupFilter>> entry : filtersMap.entrySet()) {
                    JSONObject numericFilter = new JSONObject();
                    JSONArray orJson = entry.getValue().stream().map(filter -> {
                        JSONObject filterJson = new JSONObject();
                        JSONObject rangeJson = new JSONObject();

                        if(Objects.isNull(filter.getMin())){
                            rangeJson.put("$gte",0);
                            rangeJson.put("$lte",0);
                            filterJson.put("numericFilters.value", rangeJson);
                        }

                        if(Objects.nonNull(filter.getMin()) && Objects.isNull(filter.getMax())){
                            rangeJson.put("$gte",filter.getMin());
                            rangeJson.put("$lte",filter.getMin());
                            filterJson.put("numericFilters.value", rangeJson);
                        }

                        if(Objects.nonNull(filter.getMin()) && Objects.nonNull(filter.getMax())){
                            rangeJson.put("$gte",filter.getMin());
                            rangeJson.put("$lte",filter.getMax());
                            filterJson.put("numericFilters.value", rangeJson);
                        }
                        return filterJson;
                    }).collect(Collector.of(
                            JSONArray::new, //init accumulator
                            JSONArray::put, //processing each element
                            JSONArray::put  //confluence 2 accumulators in parallel execution
                    ));
                    numericFilter.put("numericFilters.attributeId",entry.getKey());
                    numericFilter.put("$or",orJson);
                    arrayFilters.put(numericFilter);
                }
                if(!arrayFilters.isEmpty()) {
                    queryJson.put("$and",arrayFilters);
                }
            }

            JSONArray publishedJson = new JSONArray();
            if(queryJson.has("publishedForSalePerm")){
                publishedForSalePerm = queryJson.getString("publishedForSalePerm");
                queryJson.remove("publishedForSalePerm");

                JSONObject andJson = new JSONObject();
                JSONArray andArray = new JSONArray();
                JSONObject permQuery = new JSONObject();
                permQuery.put("type","PERMANENT");
                permQuery.put("isPublishedForSale",Boolean.valueOf(publishedForSalePerm));
                andArray.put(permQuery);
                andJson.put("$and",andArray);
                publishedJson.put(andJson);
            }

            if(queryJson.has("publishedForSaleRemains")){
                publishedForSaleRemains = queryJson.getString("publishedForSaleRemains");
                queryJson.remove("publishedForSaleRemains");

                JSONObject andJson = new JSONObject();
                JSONArray andArray = new JSONArray();
                JSONObject remainsQuery = new JSONObject();
                remainsQuery.put("type","REMAINING_STOCK");
                remainsQuery.put("isPublishedForSale",Boolean.valueOf(publishedForSaleRemains));
                andArray.put(remainsQuery);
                andJson.put("$and",andArray);
                publishedJson.put(andJson);
            }

            if(!publishedJson.isEmpty()) {
                queryJson.put("$or",publishedJson);
            }
            request.setQuery(queryJson.toMap());
            request.setQueryAsString(queryJson.toString());
        }

        return request;
    }
}
