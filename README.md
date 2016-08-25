# The-Seed-v1

The first iteration of the SEED Project - Minecraft based Proof-Of-Concept 

# Explanation

The Seed Project is an plan for creating a procedural world generator, where the entire world is pre-generated using no pre-made assets. 

To test this concept, and to create a working example of what I want ot achieve, I'm creating a Minecraft Bukkit plugin that will create a full-procedural RPG.

Along the way I will be recording videos explaining the concepts used, and when the code does. Each commit to this repository will have an explanation of what it is, and what video it was featured in.

# To use
When the project is completed, it will be uploaded to Curse Bukkit Plugins and the Spigot Resource list as a plugin server owners can use.

To use it, with compile it in IntelliJ and drop the jar into your plugins folder, or download a release. 

Then at the end of your bukkit.yml add: (assuming your world is called 'world')
```
worlds:
  world:
    generator: seed
```

Delete the world file if one has already been created, and start the server. It will take a bit depending on what stage the project is at, and what it's generating.

# Status Log
This area will be updated with when and what each commit added, and what video it's linked to.

* August 8th 2016: Created a simple world generator - Video coming soon - https://github.com/MusicalCreeper01/The-Seed-v1/releases/tag/1.0 - https://github.com/MusicalCreeper01/The-Seed-v1/commit/62368678b1cf6233674161d359b8e995bfa61e48
