package com.hyperfresh.nxchat.theme;

import com.hyperfresh.nxchat.HyperChat;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class YAMLTheme extends Theme
{
	private Yaml yaml;

	private String name;
	private String author;

	private String header;
	private String body;
	private String footer;

	public YAMLTheme(HyperChat hyperChat, File file) throws FileNotFoundException
	{
		super(hyperChat);

		if(file == null || !file.exists())
		{
			throw new FileNotFoundException("This file doesn't exist.");
		}

		String filename = file.getName();

		if(!FilenameUtils.getExtension(filename).equals("yml"))
		{
			throw new IllegalArgumentException("This file is not a YAML.");
		}

		yaml = new Yaml();
		Map<String, Object> map = (Map<String, Object>)yaml.load(new FileInputStream(file));

		if(map == null)
		{
			throw new IllegalArgumentException("This file cannot be parsed as a YAML.");
		}

		name = 		map.computeIfAbsent("name", (k) -> "Unknown Name").toString();
		author = 	map.computeIfAbsent("author", (k) -> "Unknown Author").toString();

		header = 	map.containsKey("header") 	? hyperChat.preprocessThemeFormat(map.get("header").toString()) : null;
		body = 	map.containsKey("body") 		? hyperChat.preprocessThemeFormat(map.get("body").toString()) : null;
		footer = 	map.containsKey("footer") 	? hyperChat.preprocessThemeFormat(map.get("footer").toString()) : null;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getAuthor()
	{
		return author;
	}

	@Override
	public String getHeaderFormat()
	{
		return header;
	}

	@Override
	public String getBodyFormat()
	{
		return body;
	}

	@Override
	public String getFooterFormat()
	{
		return footer;
	}

	public static void createDefaultThemes(File jar, File folder) throws IOException
	{
		JarFile file = new JarFile(jar);
		folder.mkdirs();
		file.stream().forEach
		(
		entry ->
		{
			if(entry.getName().startsWith("themes/") && entry.getName().endsWith(".yml"))
			{
				File outputFile = new File(folder, FilenameUtils.getName(entry.getName()));
				try
				{
					IOUtils.copy(file.getInputStream(entry), new FileOutputStream(outputFile));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		);
	}

	/**
	 * Loads all valid YAML files as themes from the specified folder.
	 *
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Theme> loadThemes(HyperChat hyperChat, File folder) throws IOException
	{
		Map<String, Theme> themes = new HashMap<>();

		if(folder.exists())
		{
			for(File file: folder.listFiles())
			{
				String ID = FilenameUtils.getBaseName(file.getName());
				try
				{
					themes.put(ID, new YAMLTheme(hyperChat, file));
				}
				catch (Exception e)
				{
					System.out.println("Skipped file " + file.getName() + ": " + e.getMessage());
				}
			}
		}
		else
		{
			throw new FileNotFoundException("Cannot find the themes folder.");
		}
		return themes;
	}
}
