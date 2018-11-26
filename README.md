# Notification add-on for OpenProdoc

The main purpose of this add-on for [OpenProdoc](https://github.com/JHierrot/openprodoc) is **to notify users and contributors when a new document is inserted through the Contribution Module**. Although it was originally developed to be used in conjunction with the Contribution Module, it can also be used as a notification system for a regular OpenProdoc installation (i.e., without any additional module enabled).

This add-on was created by [Guillermo Castellano](https://guillearch.me), from [Nosturi](https://nosturi.es).

The code is licensed under the [GNU Affero General Public License v3.0](https://github.com/guillearch/openprodoc-notification/blob/master/LICENSE).

## Functionalities

* Sending an automatic thank-you email to contributors every time they submit new content through the Contribution Module.
* Sending automatic email notifications to a specific group of OpenProdoc users if new content is submitted to a specific folder type.

## Requirements

* **OpenProdoc 2.3** or higher.
* **Java 1.8** or higher.

This add-on has been developed and tested on GNU/Linux, but it should work properly on any environment which supports Java 1.8 or higher.

## Configuration

1. Download this repository. It is not mandatory to run it in the same machine used for your OpenProdoc instance.
2. Open **ProdocRem.properties** with an editor and change the properties as needed. This file is used to establish a remote connection with the OpenProdoc database.
3. Open **notification.properties** with an editor and change the properties as needed. This file is used to define the variables of the add-on, such as the email address responsible of sending the emails, the group of users that will receive the notifications, etc.
4. Open the **OPDContribNotification** file supported by your Operating System (.sh, .command or .bat) with an editor and change the properties as needed. This file is used to start the add-on. Note that you can create different launcher scripts with different configurations. This is useful if you want to set up a task to notify contributors and another one to notify OpenProdoc users.

**Important**: If you aim to use a Gmail address to send the notifications, you must [turn on access for less secure apps](https://myaccount.google.com/lesssecureapps) in the Gmail account. For security reasons, it is highly recommended to use a dedicated address.

## Running the add-on

Start the add-on by running the OPDContribNotification file supported by your Operating System (.sh, .command or .bat).

You may want to set up a **Cron** job to run the launcher script (or scripts) at specified times and dates (e.g., every hour, once a day, etc.).

## Additional information

This documentation assumes you are already familiar with OpenProdoc administration. Otherwise, you should start by reading the official [OpenProdoc Online Help](https://jhierrot.github.io/openprodoc/help/EN/HelpIndex.html).

Please read the [changelog](https://github.com/guillearch/openprodoc-notification/tree/master/changelog.md).

If you need any further assistance, don't hesitate to [contact me](mailto:gcastellano@nosturi.es).

## Acknowledgements

This project was developed with the support of [Greenpeace Spain](https://es.greenpeace.org/es/).

Special thanks to [Joaquín Hierro](https://github.com/JHierrot/) for his invaluable help.
