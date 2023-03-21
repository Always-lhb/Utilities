// read file
private static void readFile() throws FileNotFoundException {
    String filePath = "/Users/lihb/test.txt";
    File file = new File(filePath);
    FileInputStream inputStream = new FileInputStream(file);

    InputStreamReader streamReader = new InputStreamReader(inputStream);
    BufferedReader reader = new BufferedReader(streamReader);

    String line;
    try {
        while ((line = reader.readLine()) != null) {
            // todo
        }
    } catch (IOException e) {
        LOG.warn("Read file failed.", e);
    } finally {
        IOUtils.closeQuietly(reader);
    }
}

// read resource file
private static void readResourceFile() {
    String filePath = "test.txt";
    ClassLoader classLoader = ReadFile.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(filePath);
    assert inputStream != null;
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

    String line;
    try {
        while ((line = reader.readLine()) != null) {
            // todo
        }
    } catch (IOException e) {
        LOG.warn("Read file failed.", e);
    } finally {
        IOUtils.closeQuietly(reader);
    }
}
