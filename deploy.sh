mvn clean install webstart:jnlp
rsync -avz --rsh=ssh --progress ./target/jnlp/ sat@edip:/home/sat/websites/mdbweb/