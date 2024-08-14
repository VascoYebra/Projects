# Loading the libraries
# Sys.setenv(LANG = "en") 
library(readxl)
library(shiny)
library(plotly)
library(htmltools)
library(fastmap)
library(ggplot2)
library(dplyr)
library(tidyr)

# Reading dataset
yourOwnPath = "C:\\Users\\Vasco\\Masters Business Analytics\\2Semester\\DDS\\Project\\Dataset\\PizzaSales_updated.xlsx"
pizza_data <- read_excel(yourOwnPath)

# Viewing the dataset
View(pizza_data)
pizza_data

# UI
ui <- fluidPage(
  titlePanel("Pizzeria Multivariate Analysis"),
  # Add dropdown menu at the top right corner
  fluidRow(
    column(12, align = "right",
           selectInput("chart_type", "Select Chart Type:", choices = c("price-size", "price-revenue"))
    )
  ),
  sidebarLayout(
    sidebarPanel(
      conditionalPanel(
        condition = "input.chart_type == 'price-size'",
        uiOutput("categoryFilter"),
        uiOutput("nameFilter"),
        checkboxInput("specificPizza", "Filter by specific pizza", value = FALSE)
      ),
      conditionalPanel(
        condition = "input.chart_type == 'price-revenue'",
        radioButtons("revenue_option", "Select Option:",
                     choices = c("Quantity Sold" = "quantity", "Revenue Generated" = "revenue"))
      )
    ),
    mainPanel(
      fluidRow(
        column(width = 6, align = "center",
               plotlyOutput("pie_chart", height = "400px")),  # Pie chart
        column(width = 12, align = "center",
               conditionalPanel(
                 condition = "input.chart_type == 'price-size'",
                 selectInput("plot_type", "Select Timeframe:", choices = c("Hour", "Day", "Month"))
               ),
               plotOutput("barplot_sales", height = "400px"))  # Stacked bar plot
      )
    )
  )
)

# Server
server <- function(input, output, session) {
  
  # Reactive expression to filter data by category
  categoryFilteredData <- reactive({
    req(input$categoryFilter) # Ensure the input is available
    filtered_data <- pizza_data %>% filter(pizza_category == input$categoryFilter)
    print("Category filtered data:")
    print(filtered_data)
    filtered_data
  })
  
  # Reactive expression to filter data by name
  nameFilteredData <- reactive({
    req(input$nameFilter) # Ensure the input is available
    filtered_data <- categoryFilteredData() %>% filter(pizza_name == input$nameFilter)
    print("Name filtered data:")
    print(filtered_data)
    filtered_data
  })
  
  # Dynamically update the category dropdown based on selected size
  output$categoryFilter <- renderUI({
    data <- pizza_data
    selectInput("categoryFilter", "Choose a Category:", choices = unique(data$pizza_category))
  })
  
  # Dynamically update the name dropdown based on selected category
  output$nameFilter <- renderUI({
    data <- categoryFilteredData()
    selectInput("nameFilter", "Choose a Pizza:", choices = unique(data$pizza_name))
  })
  
  # Reactive expression to calculate pizza size distribution
  pizza_size_data <- reactive({
    if (input$specificPizza) {
      nameFilteredData() %>%
        group_by(pizza_size) %>%
        summarise(total_orders = n(), .groups = 'drop')
    } else {
      categoryFilteredData() %>%
        group_by(pizza_size) %>%
        summarise(total_orders = n(), .groups = 'drop')
    }
  })
  
  # Reactive expression to calculate price-revenue distribution
  price_revenue_data <- reactive({
    pizza_data %>%
      mutate(price_category = case_when(
        unit_price < 15 ~ "< $15",
        unit_price >= 15 & unit_price <= 20 ~ "$15 - $20",
        unit_price > 20 ~ "> $20"
      )) %>%
      group_by(price_category) %>%
      group_by(price_category) %>%
      summarise(total_revenue = sum(total_price), total_quantity = n(), .groups = 'drop')
  })
  
  output$barplot_sales <- renderPlot({
    if (input$chart_type == "price-size") {
      # Get current plot_type value
      current_plot_type <- input$plot_type
      current_pizza_name <- input$nameFilter
      
      if (is.null(current_plot_type) || is.null(current_pizza_name)) {
        return(NULL)  # Return NULL if plot_type or pizza_name is NULL or not initialized
      }
      
      filtered_data <- pizza_data %>%
        filter(pizza_name == current_pizza_name)
      
      if (current_plot_type == "Hour") {
        # Aggregate data by hour of the day and calculate total pizzas sold by size
        sales_by_size_hour <- filtered_data %>%
          group_by(Hour, pizza_size) %>%
          summarise(total_pizzas = n(), .groups = 'drop') %>%
          arrange(pizza_size)  # Ensure consistent ordering for stacked bars
        
        # Create stacked bar plot using ggplot2 with custom colors
        ggplot(sales_by_size_hour, aes(x = Hour, y = total_pizzas, fill = pizza_size)) +
          geom_bar(stat = "identity", color = "black") +  # Black outline for bars
          labs(title = "Pizzas Sold by Hour of Day and Size",
               x = "Hour",
               y = "Number of Pizzas Sold",
               fill = "Pizza Size") +
          theme_minimal() +
          theme(axis.text.x = element_text(angle = 45, hjust = 1)) +  # Rotate x-axis labels
          theme(legend.position = "bottom")  # Position legend at the bottom
        
      } else if (current_plot_type == "Day") {
        # Aggregate data by day of the week and calculate total pizzas sold by size
        sales_by_size_day <- filtered_data %>%
          group_by(order_dates, pizza_size) %>%
          summarise(total_pizzas = n(), .groups = 'drop') %>%
          arrange(pizza_size)  # Ensure consistent ordering for stacked bars
        
        # Reorder days of the week for correct plotting
        sales_by_size_day$order_dates <- factor(sales_by_size_day$order_dates,
                                                levels = c("Sunday", "Monday", "Tuesday", 
                                                           "Wednesday", "Thursday", "Friday", 
                                                           "Saturday"))
        
        # Create stacked bar plot using ggplot2 with custom colors
        ggplot(sales_by_size_day, aes(x = order_dates, y = total_pizzas, fill = pizza_size)) +
          geom_bar(stat = "identity", color = "black") +  # Black outline for bars
          labs(title = "Pizzas Sold by Day of Week and Size",
               x = "Day of Week",
               y = "Number of Pizzas Sold",
               fill = "Pizza Size") +
          theme_minimal() +
          theme(axis.text.x = element_text(angle = 45, hjust = 1)) +  # Rotate x-axis labels
          theme(legend.position = "bottom")  # Position legend at the bottom
        
      } else if (current_plot_type == "Month") {
        # Aggregate data by month of the year and calculate total pizzas sold by size
        sales_by_size_month <- filtered_data %>%
          group_by(order_month, pizza_size) %>%
          summarise(total_pizzas = n(), .groups = 'drop') %>%
          arrange(pizza_size)  # Ensure consistent ordering for stacked bars
        
        # Create stacked bar plot using ggplot2 with custom colors
        ggplot(sales_by_size_month, aes(x = order_month, y = total_pizzas, fill = pizza_size)) +
          geom_bar(stat = "identity", color = "black") +  # Black outline for bars
          labs(title = "Pizzas Sold by Month of Year and Size",
               x = "Month",
               y = "Number of Pizzas Sold",
               fill = "Pizza Size") +
          theme_minimal() +
          theme(axis.text.x = element_text(angle = 45, hjust = 1)) +  # Rotate x-axis labels
          theme(legend.position = "bottom")  # Position legend at the bottom
      }
    } else {
      NULL  # Return NULL for price-revenue option
    }
  })
  
  output$pie_chart <- renderPlotly({
    if (input$chart_type == "price-size") {
      pie_chart_data <- pizza_size_data()
      plot_ly(pie_chart_data, labels = ~pizza_size, values = ~total_orders, type = 'pie',
              textinfo = 'label+percent', insidetextfont = list(color = '#FFFFFF'),
              marker = list(colors = rainbow(nrow(pie_chart_data)))) %>%
        layout(title = "Pizza Size Distribution", showlegend = TRUE)
    } else {
      pie_chart_data <- price_revenue_data()
      if (input$revenue_option == "quantity") {
        plot_ly(pie_chart_data, labels = ~price_category, values = ~total_quantity, type = 'pie',
                textinfo = 'label+percent', insidetextfont = list(color = '#FFFFFF'),
                marker = list(colors = rainbow(nrow(pie_chart_data)))) %>%
          layout(title = "Quantity Sold Distribution", showlegend = TRUE)
      } else if (input$revenue_option == "revenue") {
        plot_ly(pie_chart_data, labels = ~price_category, values = ~total_revenue, type = 'pie',
                textinfo = 'label+percent', insidetextfont = list(color = '#FFFFFF'),
                marker = list(colors = rainbow(nrow(pie_chart_data)))) %>%
          layout(title = "Revenue Generated Distribution", showlegend = TRUE)
      }
    }
  })
  
  
  
}

# Run the application
shinyApp(ui = ui, server = server)

