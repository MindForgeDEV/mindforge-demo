{
  description = "Dev Environment for MindForge Demo as Nix Flake";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.05";

  outputs = {
    self,
    nixpkgs,
  }: let
    pkgs = nixpkgs.legacyPackages.x86_64-linux;
  in {
    devShells.x86_64-linux.default = pkgs.mkShell {
      buildInputs = with pkgs; [
        jdk
        openapi-generator-cli
        gradle
        nodejs
        nodePackages.npm
      ];

      shellHook = ''
      '';
    };
  };
}
