# TikTokTech

Aplicativo Android criado durante um hackathon escolar do Instituto J&F Germinare Tech. O projeto simula uma rede social para a turma, com publicações de fotos e vídeos, interação e comentários em tempo real.

## Funcionalidades

- Cadastro e login local por e-mail e senha.
- Publicação de fotos e vídeos pela galeria ou pela câmera.
- Upload da mídia para o Cloudinary.
- Feed em tempo real com Firebase Firestore, usando a coleção `POSTS_2G`.
- Exibição do usuário que publicou cada post.
- Reprodução de vídeos dentro do feed.
- Likes e dislikes em posts e comentários: cada usuário mantém um voto por item, pode trocar de reação ou desmarcá-la.
- Comentários por publicação, com atualização automática e contador no feed.
- Interface com tema rosa.

## Tecnologias

- Java e Android Studio
- Firebase Firestore
- Cloudinary
- Glide
- Material Design 3

## Como executar

1. Abra a pasta do projeto no Android Studio.
2. Configure o Firebase com o arquivo `google-services.json` em `app/`.
3. Informe as credenciais do Cloudinary em `app/src/main/res/values/strings.xml`.
4. Compile e execute em um dispositivo Android.

## Contexto

Este repositório reúne as entregas do hackathon de Mobile LAB: publicação de mídia, identificação de usuários, feed, comentários, votos e suporte a vídeo.
Filtrar arquivos
