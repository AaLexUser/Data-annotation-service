# Data Annotation Service

A multi-modal data annotation platform designed to support rapid and high-quality labeling of various data types (texts, images, videos, point clouds, etc.) for machine learning and quality control.

---

## Table of Contents

- [Overview](#overview)
- [Problem Statement](#problem-statement)
- [Project Description](#project-description)
  - [Data & Ingestion](#data--ingestion)
  - [Annotation Workflow](#annotation-workflow)
  - [API](#api)
  - [Users & Permissions](#users--permissions)
  - [Modules](#modules)
  - [Glossary & Annotation Types](#glossary--annotation-types)
  - [Packages for Annotation](#packages-for-annotation)
  - [Interface Pages](#interface-pages)
  - [Important Considerations](#important-considerations)
- [Getting Started](#getting-started)

---

## Overview

The Data Annotation Service is built to address the growing demand for large volumes of labeled data driven by the rapid development of neural networks. It enables efficient, scalable, and accurate labeling of diverse data types, which is critical for successful model training and quality control.

---

## Problem Statement

Due to the rapid development of neural networks, there is a need for a large volume of labeled data, which may come in many forms: texts, images, videos, point clouds, etc. A service that allows fast and high-quality annotation is one of the key components for successful training of neural networks and maintaining their quality.

---

## Project Description

The project is a comprehensive service for data annotation that supports:

- **Multi-format Data Ingestion:** Accepting text fields, images, videos, etc.
- **Batch Uploads:** Data is uploaded in batches via JSON and/or archives containing media files.
- **Configurable Batch Settings:** Settings such as priority, overlaps (the number of annotations required for consensus), and assessor agreement criteria.
- **Automated Pre-annotation:** When new data is uploaded, it is compared to already annotated data in the system; if all criteria match, auto-annotation is performed.
- **Dynamic Task Assignment:** Data batches are distributed to assessors based on global priorities and individual assessor skills (e.g., only those with the specific markup type capability).
- **Multiple Evaluations:** Each item can receive several evaluations. Both all raw assessor evaluations and an aggregated final evaluation are stored.
- **Completion Tracking:** After all evaluations for a batch are complete, the batch status is updated to "completed."

### Data & Ingestion

- **Supported Data:** Text fields, images, videos, etc.
- **Upload Mechanism:**
  - **JSON** with required fields.
  - **Archives** (e.g., ZIP files) containing media files.
- **Batch Creation:** Upon upload, a batch is created with configurable settings (priority, overlap coefficient, and assessor consistency conditions). These settings can be manually adjusted after upload.

### Annotation Workflow

- **Task Distribution:** Batches are allocated to assessors based on both overall priorities and individual skill sets.
- **Multiple Assessments (Overlaps):** An item may be annotated by several assessors until the predetermined number of identical evaluations (overlaps) is reached, which then becomes the final evaluation.
- **Quality Control:** All assessor inputs are stored along with a derived final evaluation.

### API

The service exposes an API for:
- **Package Management:** Submitting annotation batches with all necessary settings.
- **Status Tracking:** Monitoring the progress and status of uploaded batches.
- **Data Retrieval:** Obtaining the finalized, annotated data.

### Users & Permissions

The system supports role-based access control:

- **Customer:** Can view only their uploaded packages and all related information.
- **Assessor:** Has access to annotations, personal profile, statistics, error logs, and the training module.
- **Expert:** Inherits all assessor privileges plus can manage all packages and view system-wide statistics.
- **Admin:** Has full system access.

### Modules

1. **Statistics Module:**
   - Calculates metrics for each assessor (number of annotations completed, average annotation time per type, error percentage, etc.).
   - Aggregates statistics across all batches (e.g., number of pending packages, total number of unannotated items per type, and progress predictions).

2. **Training Module:**
   - Allows marking a batch as "training." In each annotation within such batches, an additional text field is added to explain the correct result.
   - Displays the correct (gold standard) result after each annotation along with an explanation.
   - A copy of the training batch is created in the assessor’s profile, showing their results including counts of correct and incorrect annotations.

3. **Quality Control Module:**
   - Creates manual “honeypots” with pre-filled fields and a gold-standard evaluation.
   - These tasks are shown to all assessors based on the settings.
   - Gathers aggregate statistics on the correctness and measures the time taken for each annotation.

4. **Annotation Interface Module:**
   - Offers both JSON-based configuration and a full-featured drag-and-drop UI.
   - Provides configurable annotation logic with dependencies.
   - **Annotation Types Include:**
     - **Dependent Attributes:** Single-choice (radio button) options (e.g., correct, incorrect, unable to evaluate).
     - **Independent Fields:** Multiple-choice (checkbox) options.
     - **Conditional Fields:** Fields that appear based on previous selections.

### Glossary & Annotation Types

- **Annotation Type:** A predefined class that is set when a package is uploaded. Based on this class, the annotation interface is generated with the necessary evaluation fields.
- **Overlaps:** The number of annotations required to reach a final (consensus) evaluation.  
  _Example: For overlaps = 3, the task is sent sequentially to three different assessors until three identical evaluations are obtained, which then become the final evaluation._

#### Example Tasks:

1. **markup_type:search**

   - **Radio Button Options:**
     - `exact` – Exact
     - `complement` – Complementary
     - `substitute` – Partial
     - `irrelevant` – Useless
     - `impossible` – Unable to evaluate
     
   - **Checkbox Options:**
     - `used` – Used product
     - `counterfeit` – Counterfeit
     - `mistake` – Error in the product card

2. **markup_type:matching**

   - **Radio Button Options:**
     - `match` – Match
     - `no_match` – No match
     - `partly` – Partial
     
   - **Checkbox Options:**
     - `used` – Used product
     - `counterfeit` – Counterfeit
     - `mistake` – Error in the product card
     - `insufficient` – Insufficient data

#### Example User Setup:

- **User 1:** Assessor allowed only for annotation type `markup_type:search`.
- **User 2:** Assessor allowed only for annotation type `markup_type:matching`.
- **User 3:** Assessor allowed for both `markup_type:search` and `markup_type:matching`.
- **User 4:** Admin.

### Packages for Annotation

- **Search Package:**  
  [Google Spreadsheet](https://docs.google.com/spreadsheets/d/17sboW2jsQU7EVBKK9zz1VqO1-10gEU8dq8B5VaedfE4/edit?usp=sharing)

- **Matching Package:**  
  [Google Spreadsheet](https://docs.google.com/spreadsheets/d/18aHtDt4Uh6z-n4xMvGzyteCK84Xn_dpFzqLy_McSuY/edit?usp=sharing)

#### Data Upload Formats

1. **markup_type:search**
   - **Batch Settings:**
     - `batch_name`: `search_1`
     - `overlaps`: `3`
     - `priority`: `1`
   - **Fields:**
     - `query` – Query
     - `title` – Title
     - `url` – URL to page
     - `description` – Text description
     - `url_photo` – URL to photo

2. **markup_type:matching**
   - **Batch Settings:**
     - `batch_name`: `matching_1`
     - `overlaps`: `1`
     - `priority`: `1`
   - **Fields:**
     - For **Product 1:** `title1`, `url1`, `description1`, `url_photo1`
     - For **Product 2:** `title2`, `url2`, `description2`, `url_photo2`

> **Note:** In addition to text data, images can be added either via URLs or by uploading them to the system. On the product card, the uploaded images are displayed with carousel navigation.

### Interface Pages

- **Annotation Type List Page:**  
  Displays a list of annotation types along with their creation dates. Clicking on a name navigates to the detailed view of the annotation type. There is also the ability to add a new type via JSON or a graphical interface that lets you set up the required logic and dependencies.

- **Uploaded Packages List Page:**  
  Accessible only to admins, this page displays a list of all uploaded batches, showing the current status of each package. It also allows managing the task queue and toggling batches on or off.

- **Batch Detail Page:**  
  Allows direct management of package settings (e.g., renaming, enabling/disabling) and viewing each task with all assessor evaluations. It also supports exporting the results as JSON.

- **Task Results Page:**  
  Shows the task as it appears to the assessor along with the final evaluation, which can be adjusted. When adjusted, the evaluation receives an additional "admin verified" status. A table lists all assessor results for the task.

- **Assessor Annotation Page:**  
  After logging in, the assessor sees a statistics widget (daily/monthly) and a task card. Once the task is saved, the data is stored, the widget is updated, and the next task is loaded. Assessors have a fixed time (N minutes) to correct a task, and tasks available for correction display a "correct" button.

- **Assessor Profile Page:**  
  Displays detailed statistics for the assessor (total annotations completed, earnings, errors, progress, etc.). When viewed by an admin, additional detailed information is presented.

- **Assessor List & Statistics Page (Admin View):**  
  Presents a summary table of all assessors in the system with the ability to filter by annotation type and a selectable date range.

---

## Important Considerations

1. **Scalability:**  
   The service is designed to scale with both the number of assessors (up to 500) and the number of annotations (up to 10 million per month). This must be considered when designing the database and overall system architecture.

2. **Multiple Evaluations:**  
   Each task can have several evaluations with different statuses — the raw evaluations by assessors and the final aggregated evaluation. The system’s architecture must account for and efficiently handle these relationships.

---

## Getting Started

Follow these steps to set up the project locally.

...

