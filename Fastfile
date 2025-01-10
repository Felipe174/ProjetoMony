default_platform(:android)

platform :android do
  desc "Build and deploy the Android application"
  lane :deploy do
    gradle(task: "clean")  # Limpa o projeto
    gradle(task: "assembleRelease")  # Compila a versão de release

    upload_to_play_store(  # Faz o upload para o Google Play
      track: 'internal',  # Define o track como interno
      json_key: "./path/to/your/google-play-credentials.json",  # Caminho para o arquivo JSON de credenciais
      package_name: "com.example.myamazingapp",  # Nome do pacote do seu aplicativo
      aab: "./app/build/outputs/bundle/release/app-release.aab"  # Caminho para o arquivo AAB gerado
    )
  end
end