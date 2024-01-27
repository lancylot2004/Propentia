{ name = "propentia"
, dependencies =
  [ "exceptions"
  , "foldable-traversable"
  , "lists"
  , "maybe"
  , "prelude"
  , "tuples"
  ]
, packages = ./packages.dhall
, sources = [ "src/logic/**/*.purs", "src/logic/test/**/*.purs" ]
}
