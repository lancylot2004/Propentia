{ name = "propentia"
, dependencies =
  [ "arrays"
  , "console"
  , "effect"
  , "foldable-traversable"
  , "lists"
  , "maybe"
  , "prelude"
  ]
, packages = ./packages.dhall
, sources = [ "src/logic/**/*.purs", "src/logic/test/**/*.purs" ]
}
