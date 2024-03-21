package com.t4a.examples;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

import java.io.IOException;

public class SimplePredictionExample {
    public static void main(String[] args) {
        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";
        String preaction = "here is my prompt - ";
        String action = "- what action do you think we should take insertNewEmployeeInDB , bookRentalCar, bookAirwayTicket, eatFood, sendMessageToTibco, sendMyCarForServicing, openJiraTicket, bookRestaurentReservation, bookMovieTicket, findRecipeOnInternet, getGrocery, reply back with one action only";
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerateContentResponse response;

            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            ChatSession chatSession = new ChatSession(model);

            response = chatSession.sendMessage(preaction+"Hey new new employee joined today his name is Shahruh Devgan, his id is 788778"+action);
            System.out.println(ResponseHandler.getText(response));

            response = chatSession.sendMessage(preaction+"I want to eat really spicy Indian Food Today?"+action);
            System.out.println(ResponseHandler.getText(response));

            response = chatSession.sendMessage(preaction+"What is the recipe for Paneer Butter Masala?"+action);
            System.out.println(ResponseHandler.getText(response));

            response = chatSession.sendMessage(preaction+"I need to go from Toronto to Bangalore?"+action);
            System.out.println(ResponseHandler.getText(response));

            response = chatSession.sendMessage(preaction+"I need to go from Toronto to Monreal?"+action);
            System.out.println(ResponseHandler.getText(response));

            response = chatSession.sendMessage("What is the current weather in Toronto? here is additional information null- what action do you think we should take sendMessageToQueue,whatFoodDoesThisPersonLike,insertCustomerComplaint,googleSearch,saveInformationToLocalFile,getTemperature,saveEmployeeInformation,raiseTicketForProductionIssue,runBackup,runAutomatedTests,startService,restartCustomerPortal,diagnoseEngineIssues,alertSupportTeam,backupDatabase,scanSystemVulnerabilities,getUserDetails, somethingNotVeryUseful, ,getActivities,postActivities,getActivities_With_id,putActivities,deleteActivities,getAuthors,postAuthors,getbooks,getAuthors_With_id,putAuthors,deleteAuthors,getBooks,postBooks,getBooks_With_id,putBooks,deleteBooks,getCoverPhotos,postCoverPhotos,getcovers,getCoverPhotos_With_id,putCoverPhotos,deleteCoverPhotos,getUsers,postUsers,getUsers_With_id,putUsers,deleteUsers,updatePet,addPet,findPetsByStatus,findPetsByTags,getPetById,updatePetWithForm,deletePet,uploadFile,getInventory,placeOrder,getOrderById,deleteOrder,createUser,createUsersWithListInput,loginUser,logoutUser,getUserByName,updateUser,deleteUser - reply back with 1 action only");
            System.out.println(ResponseHandler.getText(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
