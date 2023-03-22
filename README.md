# FSit (Fabric)

A server-side mod that allows players to sit anywhere.

___NOTE: so that the players do not block your view install client-side mod__ (check additional files; bundled in full
version)_

## Usage

To sit down, look down and sneak twice.
To get up, just sneak once.  
The `/sit` command to toggle.  
Also, right-click when sneaking on stairs, slabs or horizontal logs (configurable) works.

### Configuration (stored in `config/fsit.toml`)

```toml
min_angle = 66.0 # degrees. Minimal pitch to sitting down.
shift_delay = 600 # milliseconds. Time between sneaks for sitting down.
sittable_blocks = [] # List of block ids (e.g. "oak_log") available to sit.
sittable_tags = ["minecraft:slabs", "stairs", "logs"] # List of block tags.
sit_on_players = false # Ability to sit on other players
```

## Contributing

Pull requests are welcome.  
For major changes, please open an issue first to discuss what you would like to change.

## License

This project is licensed under the [MIT License][license].

[license]: ./LICENSE
