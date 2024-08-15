######################################################### Cambridge University Press ########################################### 

#------------------------Functions------------------------#
# Loads the required libraries for the project
LoadDependencies <- function(){
  pacman::p_load("tidyverse","rvest","stringr","stringr", "rebus", "lubridate", "xml2")
  library(tidyverse)
  library(rvest)
  library(stringr)
  library(httr)
  install.packages("httr")
  library(httr)
}

# Clears the workspace of variables and other junk
ClearWorkSpace = function(){
  rm(list=ls())
  graphics.off()
}

# Gets the title of an article, given a individual link html
getTitle = function(individualPageHTML){
  Title = html_nodes(individualPageHTML, xpath = "//div[@class='column__main__left']/div/hgroup/h1")
  TitleText = html_text(Title)
  return = TitleText
}

# Gets the Year of publication of an article, given an individual link html
getYear = function(individualPageHTML){
  Year = html_nodes(individualPageHTML, xpath = "//div[@class='column__main__left']/div/div/p")
  YearText = html_text(Year)
  
  #Becase we just want to return the year, we need a substring
  Year = str_sub(YearText, -5, -1)
  return = gsub("\n", "", Year) # sometimes necessary to clean the string
}


# Gets the article DOI. Some papers have a different class name; instead of article details they have chapter details
getDOI = function(individualPageHTML){
  DOI = html_nodes(individualPageHTML, xpath = "//dl[@class='article-details']")
  if( length(DOI) == 0 ){
    DOI = html_nodes(individualPageHTML, xpath = "//dl[@class='chapter-details']")
  }
  DOIText <- html_text(DOI)
  
  # Retrieving the doi url. regmatches extracts substrings using a regular expressions. 
  #the //S+ captures every character afterwards until a whitespace is found!
  DOILink = regmatches(DOIText, regexpr("https://doi.org/\\S+", DOIText))
  
  return = DOILink
}

# Retrieves the Volume number.
getVolume = function(individualPageHTML){
  Volume = html_nodes(individualPageHTML, xpath = "//dl[@class='article-details']")
  if( length(Volume) == 0 ){
    Volume = html_nodes(individualPageHTML, xpath = "//dl[@class='chapter-details']")
  }
  VolumeText = html_text(Volume)
  
  # Retrieving the doi url. regmatches extracts substrings using a regular expressions. 
  # The //S+ captures every character afterwards until a whitespace is found!
  VolumeNumber = regmatches(VolumeText, regexpr("Volume \\S+", VolumeText))
  
  # Only need the number part of the string. the regular expression means that, if not a character between 0-9, then 
  # Replace with with "", or empty
  VolumeNumber = gsub("[^0-9]", "", VolumeNumber)
  
  return = VolumeNumber
}

getJournal <- function(individualPageHTML){
  Journal <- html_nodes(individualPageHTML, xpath = "//a[@class='app-link app-link__text app-link--underlined']")
  
  # Extract Journal Name
  journal_name <- html_text(Journal)
  
  # Create abbreviation by taking the first letter of each word
  #abbreviation <- paste0(substr(strsplit(journal_name, " ")[[1]], 1, 1), collapse = "")
  
  if (length(journal_name) > 0 && !any(is.na(journal_name))) {
    # Split the first element of the vector
    abbreviation <- paste0(substr(strsplit(journal_name, " ")[[1]], 1, 1), collapse = "")
  } else {
    abbreviation = "Error"
  }
  
  return(abbreviation)
  
}

#Retrieves Abstract
getAbstract <- function(individualPageHTML){
  Abstract <- html_nodes(individualPageHTML, xpath = "//div[@id='sec0']//div[@class='abstract-content']//p")
  
  # Extract Abstract Text
  abstract_text <- html_text(Abstract)
  # Check if abstract nodes are found
  if (length(Abstract) > 0) {
    # Extract Abstract Text
    abstract_text <- html_text(Abstract)
    return(abstract_text)
  } else {
    # Return "no abstract" if no abstract nodes are found
    return("no abstract")
  }
}


# Retrieves Content
getContent <- function(individualPageHTML){
  # Adjust the XPath based on the actual structure of the webpage
  Content <- html_nodes(individualPageHTML, xpath = "//div[@class = 'article research-article NLM']")
  
  # Extract text content
  Content_text <- html_text(Content, trim = TRUE)
  
  return(Content_text)
}

# Retrieves JEL Classification
getJEL <- function(individualPageHTML) {
  JEL <- html_nodes(individualPageHTML, xpath = "//div[@class='classification__type']/div[@class= 'row']/a")
  JEL_text <- html_text(JEL)
  
  #Removing \n
  JEL_text_clean = gsub("\n", "", JEL_text)
  
  #Extract the first two characters from each line
  JEL_short <- substr(JEL_text_clean, 1, 2)
  
  return(JEL_short)
  
}

# Get the Issue
getIssue <- function(individualPageHTML){
  Issue <- html_nodes(individualPageHTML, xpath = "//dl[@class='article-details']")
  if( length(Issue) == 0 ){
    Issue <- html_nodes(individualPageHTML, xpath = "//dl[@class='chapter-details']")
  }
  IssueText <- html_text(Issue)
  
  # The //S+ captures every character afterwards until a whitespace is found!
  IssueNumber <- regmatches(IssueText, regexpr("Issue \\S+", IssueText))
  
  # Only need the number part of the string. the regular expression means that, if not a character between 0-9, then 
  # Replace with with "", or empty
  IssueNumber <- gsub("[^0-9]", "", IssueNumber)
  
  return <- IssueNumber
  
}


# Retrieves the Affiliation
getAffiliation <- function(individualPageHTML){
  Affiliation <- html_nodes(individualPageHTML, xpath =  "//dl[@class='authors-details collapse']//dd[span[@class='content__title' and text()='Affiliation:']]")
  AffiliationText <- html_text(Affiliation)
  
  # Split the text after \n
  AffiliationText <- unlist(strsplit(AffiliationText, "\n"))
  
  #Remove everything before : 
  Affiliation <- gsub(".*:\\s*", "", AffiliationText)
  
  return <- Affiliation
  
}

# Retrieves the Authors
getAuthors <- function(individualPageHTML){
  Authors <- html_nodes(individualPageHTML, xpath = "//div[@class='contributor-type__contributor']")
  
  AuthorsText <- html_text(Authors)
  
  #Replace and and remove spaces
  AuthorsText <- gsub("\\s*and$", "", AuthorsText)
  AuthorsText <- trimws(gsub("\\s*,*$", "", AuthorsText))
  AuthorsText <- str_trim(str_replace_all(AuthorsText, "\\s*and\\s*", ","))
  
  return(AuthorsText)
}

# Retrieves the links for all volumes of a specific journal
getAllVolumes = function(individualJournalHTML){
  
  journalHTML = html_nodes(individualJournalHTML, xpath = "//li[@class='accordion-navigation']/ul/li")
  JournalText = html_text(journalHTML)
  
  #get the tag "a"
  anchorTags = html_nodes(journalHTML, "a")
  
  #get the href attribute, since it is the one that has the link
  listLinksPartial = html_attr(anchorTags, "href")
  
  #create a empty variable that will be a list of the full links
  fullLinksList = ""
  
  #now join the first part of the link with the part that changes
  for( i in seq_along(listLinksPartial) )
  {
    fullLinksList[i] = paste0(websiteStartAddress, listLinksPartial[i] )
  }
  
  return = fullLinksList
}

# Retrieves all the articles links given a volume page
getAllArticles = function(VolumeLinkHTML){
  # filtering with the xpath expression to narrow down our search
  ArticleHTML = html_nodes(VolumeLinkHTML, xpath = "//ul[@class='details']/li/h3")
  
  #get the tag "a"
  anchorTags = html_nodes(ArticleHTML, "a")
  
  #get the href attribute, since it is the one that has the link
  listArticlePartial = html_attr(anchorTags, "href")
  
  #create a empty variable that will be a list of the full links
  fullArticleList = ""
  
  #now join the first part of the link with the part that changes
  for( i in seq_along(listArticlePartial) )
  {
    # if the part of our link does not contain core/jounral, then some other error or wrong link was obtained, thus we skip it
    if( grepl("#", listArticlePartial[i], fixed = TRUE ) ) next
    fullArticleList[i] = paste0(websiteStartAddress, listArticlePartial[i] )
  }
  
  return = fullArticleList
}

# Checks if there are duplicates
find_duplicate_links <- function(link_list) {
  # Use duplicated to identify duplicate entries
  duplicates <- duplicated(link_list)
  
  # Get the indexes of the duplicated entries
  duplicate_indexes <- which(duplicates)
  
  return(duplicate_indexes)
}

# to remove duplicated data
removeDuplicateLinks <- function(links) {
  uniqueLinks <- unique(links)
  return(uniqueLinks)
}

#removes non-valid linkds
removeLinksWithoutOrgCore <- function(links) {
  #use grep to find indices of links that contain ".org/core"
  indices_to_keep <- grep(".org/core", links)
  
  #subset the original list to keep only the desired links
  cleaned_links <- links[indices_to_keep]
  
  return(cleaned_links)
}


# Removes NA from list, makes the list shorter
remove_na_from_list <- function(my_list) {
  return(my_list[!sapply(my_list, function(x) any(is.na(x)))])
}


#------------------End------------------------------------#
ClearWorkSpace()
LoadDependencies()

######## Looping through all the artciles in one journal ##############

#We can assume this information will not change
websiteStartAddress = "https://www.cambridge.org"
AllJournalsLinks = c(
  #"https://www.cambridge.org/core/journals/astin-bulletin-journal-of-the-iaa/all-issues"
  #"https://www.cambridge.org/core/journals/econometric-theory/all-issues"
  #"https://www.cambridge.org/core/journals/the-economic-and-labour-relations-review/all-issues"
  #"https://www.cambridge.org/core/journals/economics-and-philosophy/all-issues"
  #"https://www.cambridge.org/core/journals/environment-and-development-economics/all-issues"
  #"https://www.cambridge.org/core/journals/journal-of-demographic-economics/all-issues"
  #"https://www.cambridge.org/core/journals/journal-of-economic-history/all-issues"
  #"https://www.cambridge.org/core/journals/journal-of-financial-and-quantitative-analysis/all-issues"
  #"https://www.cambridge.org/core/journals/journal-of-institutional-economics/all-issues"
  #"https://www.cambridge.org/core/journals/journal-of-pension-economics-and-finance/all-issues"
  #"https://www.cambridge.org/core/journals/journal-of-wine-economics/all-issues"
  #"https://www.cambridge.org/core/journals/macroeconomic-dynamics/all-issues"
  #"https://www.cambridge.org/core/journals/revista-de-historia-economica-journal-of-iberian-and-latin-american-economic-history/all-issues"
  #"https://www.cambridge.org/core/journals/world-trade-review/all-issues"
  #"https://www.cambridge.org/core/journals/journal-of-benefit-cost-analysis/all-issues" #journal has issues, the read_html does not read the body of this html for some reason
  )

Everything = list()

for (individualJournal in AllJournalsLinks) {
  
  individualJournalHTML = read_html(individualJournal)
  
  if (length(html_nodes(individualJournalHTML, "body")) == 0)
  {
    cat("The page has no body! Skipping this journal: ",individualJournal)
    next
  }
  else{
    #print("Page has a body....continuing ")
    
    #Volume Links is a list that will contain all the Volume Link pages
    VolumeLinks = getAllVolumes(individualJournalHTML)
    
    # the volume page has several links for individual article, in this step we retrieve each one of those links. approx 5 mins
    AllArticlesSingleJournal = list()
    for( i in seq_along(VolumeLinks)){
      cat("Current Volume number: ", i, "\n") ###### FOR TESTING PURPOSES ########
      
      #while the error is 200 then its ok
      while(TRUE)
      {
        tryCatch({
          response <- GET( VolumeLinks[i] )
          
          #get the status code
          status_code <- httr::status_code(response)
          cat("Status Code: ", status_code, "\n")
          
          # error 200 means that the request is successfull
          if ( status_code == 200 ) {
            #cat("504 Gateway Timeout Error for URL:", VolumeLinks[i], "\n")
            #Sys.sleep(30)
            print("Inside IF")
            
            singleVolumeHTMl = read_html(VolumeLinks[i])
            
            #the list of links in each volume page
            ArticlesInSingleVolume = getAllArticles( singleVolumeHTMl )
            
            #gathering the current articles in one place and cleaning junk
            AllArticlesSingleJournal = append(AllArticlesSingleJournal, ArticlesInSingleVolume)
            AllArticlesSingleJournalCleaned = remove_na_from_list(AllArticlesSingleJournal)
            
            #Everything = append(Everything,ArticlesInSingleVolume)
            Everything = append(Everything, Filter(function(x) !grepl("cover-and-back-matter|cover-and-front-matter|Back Cover", x), AllArticlesSingleJournalCleaned))
            
            #if this links was sucessfull, move on the next one by breaking the current while (true)
            break
            print("point2")
          }
          else{
            #if i have some sort of error, then sleep 30 seconds and try again
            Sys.sleep(30)
          }
        })
      }
      
    }
  }
  
}

EverythingCleaned = removeDuplicateLinks(Everything)
EverythingCleanedOnlyValidLinks = removeLinksWithoutOrgCore(EverythingCleaned)
EverythingCleanedNoNa = remove_na_from_list(EverythingCleanedOnlyValidLinks)

#Now for the final step, we go through a dataframe and use our functions to scrap the data !

#create the dataframe
SingleJournalDF <- data.frame(Author = character(), Title = character(), JEL = character(), 
                                      Journal = character(), Year = character(), Volume = character(),
                                      Issue = character(), DOI = character(), Affliliation = character(),
                                      Abstract = character(), Content = character(),
                                      stringsAsFactors = FALSE)

for (link in EverythingCleanedNoNa) {
  # Perform web scraping using your functions
  currentArticleHTML = read_html(link)
  #currentArticleHTML = read_html(curl('http://google.com', handle = curl::new_handle("useragent" = "Mozilla/5.0")))
  author = getAuthors(currentArticleHTML)            #
  title = getTitle(currentArticleHTML)               #
  jel = getJEL(currentArticleHTML)                   #
  journal = getJournal(currentArticleHTML)           #
  year = getYear(currentArticleHTML)                 #
  volume = getVolume(currentArticleHTML)             #
  issue = getIssue(currentArticleHTML)               # 
  doi = getDOI(currentArticleHTML)                   #
  affliliation = getAffiliation(currentArticleHTML)  #
  abstract = getAbstract(currentArticleHTML)
  content = getContent(currentArticleHTML)
  
  #cat("title: ", title, "/n")
  #cat("year: ", year, "/n")
  #cat("affliliation: ", affliliation, "/n")
  
  #some columns can have multiple values and thus it's necessary to split them up
  if( length(author) > 1 )
  {
    #new author value
    author = unlist(strsplit(author, ", "))
    author <- paste(author, collapse = ", ")
  }
  
  if( length(affliliation) > 1 )
  {
    affliliation = unlist(strsplit(affliliation, ", "))
    affliliation <- paste(affliliation, collapse = ", ")
  }
  
  cat("Information for the link: ", link, " gathered! \n")
  
  #creating a list that will be our new row in the dataframe
  currentRow = list(
    Author = author,
    Title = title,
    JEL = jel,
    Journal = journal,
    Year = year,
    Volume = volume,
    Issue = issue,
    DOI = doi,
    Affiliation = affliliation,
    Abstract = abstract,
    Content = content
  )
  
  #convert the row into a single row dataframe so we can use rbind and merge both dataframes!
  currentRowDF <- as.data.frame(t(currentRow))
  
  #each iteration update the FInalDF1
  SingleJournalDF = rbind(SingleJournalDF, currentRowDF )
  
  Sys.sleep(10)
  
  
}

#export the DF, increment the filename by 1 for each successive journal
save(SingleJournalDF, file = "SingleJournalDF1.RData")








