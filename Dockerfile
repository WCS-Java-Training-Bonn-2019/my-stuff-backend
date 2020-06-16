FROM ubuntu:latest
MAINTAINER David Beisert beisdog@web.de

RUN apt-get -y update
RUN apt-get -y upgrade 
RUN apt-get install -y build-essential
RUN apt-get install -y openjdk-11-jdk-headless

RUN echo "deb http://cn.archive.ubuntu.com/ubuntu/ xenial main restricted universe multiverse" >> /etc/apt/sources.list

RUN echo "mysql-server mysql-server/root_password password root" | debconf-set-selections
RUN echo "mysql-server mysql-server/root_password_again password root" | debconf-set-selections

RUN apt-get update && \
	apt-get -y install mysql-server-5.7 && \
	mkdir -p /var/lib/mysql && \
	mkdir -p /var/run/mysqld && \
	mkdir -p /var/log/mysql && \
	chown -R mysql:mysql /var/lib/mysql && \
	chown -R mysql:mysql /var/run/mysqld && \
	chown -R mysql:mysql /var/log/mysql


# UTF-8 and bind-address
RUN sed -i -e "$ a [client]\n\n[mysql]\n\n[mysqld]"  /etc/mysql/my.cnf && \
	sed -i -e "s/\(\[client\]\)/\1\ndefault-character-set = utf8/g" /etc/mysql/my.cnf && \
	sed -i -e "s/\(\[mysql\]\)/\1\ndefault-character-set = utf8/g" /etc/mysql/my.cnf && \
	sed -i -e "s/\(\[mysqld\]\)/\1\ninit_connect='SET NAMES utf8'\ncharacter-set-server = utf8\ncollation-server=utf8_unicode_ci\nbind-address = 0.0.0.0/g" /etc/mysql/my.cnf

VOLUME /var/lib/mysql

COPY ./mysql_startup.sh /root/mysql_startup.sh
RUN chmod +x /root/mysql_startup.sh

#ENTRYPOINT ["/root/mysql_startup.sh"]

#EXPOSE 3306
#CMD ["/usr/bin/mysqld_safe"]

# Mystuff
COPY ./target/my-stuff-backend-0.0.1-SNAPSHOT.jar /root/app.jar
COPY ./run_all.sh /root/run_all.sh
RUN chmod +x /root/run_all.sh
ENTRYPOINT ["/root/run_all.sh"]
EXPOSE 8080
EXPOSE 3306
#CMD ["/usr/bin/mysqld_safe"]
