package com.t4a.examples;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class OpenAiTester {
    public static void main(String[] args) {

        ChatLanguageModel model = OpenAiChatModel.withApiKey("demo");

        String joke = model.generate("here is your prompt - Can I go out without jacket in toronto today - what action should you take from the list of action - getTemperature,getName,postActivity - reply back with one action only");

        System.out.println(joke);
        joke = model.generate("here is your prompt - Can I go out without jacket in toronto today - here is you action- getTemperature(cityName,province,country) - what parameter should you pass to this function. give comma separated values only and nothing else");
        System.out.println(joke);

        joke = model.generate("here is your prompt - my friend Vinod is visiting me from Balaghat , I want to take him out to for food , not sure what he will eat since I am south Indian based in Bangalore - what action should you take from the list of action - getTemperature,getName,postActivity,findRestaurant,reserveRestaurant,cookFood - reply back with one action only");

        System.out.println(joke);

        joke = model.generate("here is your prompt -my friend Vinod is visiting me from Balaghat , I want to take him out to for food , not sure what he will eat since I am south Indian based in Bangalore  - here is you action- findRestaurant(numOfPeople,city,country) - what parameter should you pass to this function. give comma separated name=values only and nothing else");
        System.out.println(joke);
        joke = model.generate("here is my prompt - My friends name is Vishal ,I dont know what to cook for him today.- which of these actions should you take -  whatFoodDoesThisPersonLike(),getTemperature,saveEmployeeName,raiseTicketForProductionIssue,runBackup,runAutomatedTests,startService,restartCustomerPortal,diagnoseEngineIssues,alertSupportTeam,backupDatabase,scanSystemVulnerabilities,getUserDetails, getSomethignUseful, ,getActivities,postActivities,getActivities_With_id,putActivities,deleteActivities,getAuthors,postAuthors,getbooks,getAuthors_With_id,putAuthors,deleteAuthors,getBooks,postBooks,getBooks_With_id,putBooks,deleteBooks,getCoverPhotos,postCoverPhotos,getcovers,getCoverPhotos_With_id,putCoverPhotos,deleteCoverPhotos,getUsers,postUsers,getUsers_With_id,putUsers,deleteUsers,updatePet,addPet,findPetsByStatus,findPetsByTags,getPetById,updatePetWithForm,deletePet,uploadFile,getInventory,placeOrder,getOrderById,deleteOrder,createUser,createUsersWithListInput,loginUser,logoutUser,getUserByName,updateUser,deleteUser - reply back with 1 action only");
        System.out.println(joke);
    }
}
